import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable} from 'rxjs';
import {UserProfile} from "../models/user-profile.model";
import {OAuthEvent, OAuthService} from "angular-oauth2-oidc";
import {Router} from "@angular/router";

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
      private router: Router
  ) {
    this.isAdmin$ = this.userProfile$.pipe(
        map(profile => profile?.roles?.includes('ROLE_ADMIN') ?? false)
    );

    this.initializeAuthService();
  }

  private initializeAuthService(): void {
    this.oauthService.events.subscribe((event: OAuthEvent) => {
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
      }
    });

    if (this.oauthService.hasValidAccessToken()) {
      this.handleAuthenticationSuccess();
    }
  }

  public login(): void {
    this.oauthService.initLoginFlowInPopup();
  }

  public logout(): void {
    this.oauthService.logOut();
    this.handleAuthenticationFailure();
    this.router.navigate(['/login']);
  }

  public getAccessToken(): string | null {
    return this.oauthService.getAccessToken();
  }

  public hasValidAccessToken(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  public getUserProfile(): UserProfile | null {
    return this.userProfileSubject.value;
  }

  private async handleAuthenticationSuccess(): Promise<void> {
    this.isAuthenticatedSubject.next(true);

    try {
      const claims = this.oauthService.getIdentityClaims() as any;
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
      }
    } catch (error) {
      console.error('Błąd podczas ładowania profilu użytkownika:', error);
    }
  }

  private handleAuthenticationFailure(): void {
    this.isAuthenticatedSubject.next(false);
    this.userProfileSubject.next(null);
  }
}
