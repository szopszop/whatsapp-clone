import {Injectable, NgZone} from '@angular/core';
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
    private router: Router,
    private zone: NgZone
  ) {
    this.isAdmin$ = this.userProfile$.pipe(
      map(profile => profile?.roles?.includes('ROLE_ADMIN') ?? false)
    );

    this.initializeAuthService();
    this.setupPopupMessageListener();

    this.checkInitialAuthState();
  }

  private checkInitialAuthState(): void {
    if (this.oauthService.hasValidAccessToken()) {
      this.handleAuthenticationSuccess();
    }
  }

  private initializeAuthService(): void {
    this.oauthService.events.subscribe((event: OAuthEvent) => {
      console.log('OAuth Event:', event.type);
      switch (event.type) {
        case 'token_received':
        case 'token_refreshed':
          console.log('Token received/refreshed - updating auth state');
          this.handleAuthenticationSuccess();
          break;
        case 'logout':
        case 'token_expires':
        case 'token_error':
          console.log('Auth failure event - updating auth state');
          this.handleAuthenticationFailure();
          break;
      }
    });
  }

  private setupPopupMessageListener(): void {
    window.addEventListener('message', (event) => {
      if (event.origin !== window.location.origin) {
        return;
      }

      if (event.data && event.data.type === 'oauth-callback') {
        console.log('Received popup callback:', event.data);

        this.zone.run(() => {
          this.oauthService.tryLogin({
            customHashFragment: event.data.search || event.data.hash
          }).then(() => {
            if (this.oauthService.hasValidAccessToken()) {
              console.log('Login successful - updating auth state');
              this.handleAuthenticationSuccess();
              this.router.navigate(['/']);
            } else {
              this.handleAuthenticationFailure();
            }
          }).catch((error) => {
            this.handleAuthenticationFailure();
          });
        });
      }
    });
  }

  public async login(): Promise<void> {
    try {
      await this.oauthService.initLoginFlowInPopup({height:500,width:600,});
    } catch (error) {
      console.error('Popup login error:', error);
    }
  }

  public register(): void {
    const registerUrl = `${this.oauthService.issuer}/register`;
    window.open(registerUrl, 'popup', 'width=600,height=700');
  }

  public logout(): void {
    this.oauthService.logOut();
    //this.router.navigate(['/login']);
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
    console.log('Handling authentication success...');
    this.isAuthenticatedSubject.next(true);

    try {
      const claims = this.oauthService.getIdentityClaims() as any;
      console.log('Identity claims:', claims);

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
        console.log('User profile updated:', userProfile);
      }
    } catch (error) {
      console.error('Błąd podczas ładowania profilu użytkownika:', error);
    }
  }

  private handleAuthenticationFailure(): void {
    console.log('Handling authentication failure...');
    this.isAuthenticatedSubject.next(false);
    this.userProfileSubject.next(null);
  }
}
