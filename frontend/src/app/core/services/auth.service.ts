import {Injectable, NgZone} from '@angular/core';
import {BehaviorSubject, map, Observable} from 'rxjs';
import {UserProfile} from "../models/user-profile.model";
import {OAuthEvent, OAuthService} from "angular-oauth2-oidc";
import {Router} from "@angular/router";
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {RegisterRequest} from '../models/register-request.model';
import {IdentityClaims} from '../models/identity-claims.model';

import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private userProfileSubject = new BehaviorSubject<UserProfile | null>(null);
  public userProfile$ = this.userProfileSubject.asObservable();

  public isAdmin$: Observable<boolean>;

  constructor(
    private oauthService: OAuthService,
    private router: Router,
    private http: HttpClient,
    private ngZone: NgZone,
    private toastr: ToastrService

  ) {
    this.isAdmin$ = this.userProfile$.pipe(
      map(profile => profile?.roles?.includes('ROLE_ADMIN') ?? false)
    );

    this.initializeAuthService();
  }

  private initializeAuthService(): void {
    this.oauthService.events.subscribe((event: OAuthEvent) => {
      this.ngZone.run(() => {
        switch (event.type) {
          case 'token_received':
          case 'token_refreshed':
            this.handleAuthenticationSuccess();
            break;
          case 'logout':
          case 'token_expires':
          case 'token_error':
            this.handleAuthenticationFailure();
            break;
          case 'discovery_document_loaded':
            if (this.oauthService.hasValidAccessToken()) {
              this.handleAuthenticationSuccess();
            }
            break;
        }
      });
    });

    if (this.oauthService.hasValidAccessToken()) {
      this.handleAuthenticationSuccess();
    }
  }


  public login(): void {
    this.oauthService.initCodeFlow();
  }

  public logout(): void {
    this.oauthService.logOut();
    this.handleAuthenticationFailure();
    this.router.navigate(['/']);
  }

  private async handleAuthenticationSuccess(): Promise<void> {

    if (this.isAuthenticatedSubject.value) {
      return;
    }

    this.isAuthenticatedSubject.next(true);
    this.toastr.success('Logged in successfully');

    try {
      const claims = this.oauthService.getIdentityClaims() as IdentityClaims;
      if (claims) {
        const userProfile: UserProfile = {
          sub: claims.sub,
          name: claims.name || claims.preferred_username,
          email: claims.email,
          preferred_username: claims.preferred_username,
          email_verified: claims.email_verified || false,
          roles: claims.roles || []
        };
        this.userProfileSubject.next(userProfile);
        this.router.navigate(['/chat']);
      }
    } catch (error) {
      this.toastr.error('Error occurred while loading user profile');

    }
  }

  private handleAuthenticationFailure(): void {
    this.isAuthenticatedSubject.next(false);
    this.userProfileSubject.next(null);
  }

  navigateToRegisterPage() {
    this.router.navigate(['/register']);
  }

  registerApi(registerRequest: RegisterRequest): Observable<any> {
    return this.http.post(`${environment.authServerUrl}/api/v1/auth/register`, registerRequest);
  }
}
