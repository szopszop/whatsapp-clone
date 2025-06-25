import {Injectable, NgZone} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {OAuthEvent, OAuthService} from 'angular-oauth2-oidc';
import {BehaviorSubject, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {environment} from '../../../environments/environment';

export interface UserProfile {
  sub: string;
  name: string;
  email: string;
  preferred_username: string;
  email_verified: boolean;
  roles: string[];
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private userProfileSubject = new BehaviorSubject<UserProfile | null>(null);
  public userProfile$ = this.userProfileSubject.asObservable();

  public isAdmin$: Observable<boolean>;

  private pendingPopup: Window | null = null;

  constructor(
    private oauthService: OAuthService,
    private router: Router,
    private zone: NgZone,
    private http: HttpClient
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
      console.log('OAuth Event:', event.type, event);

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
        case 'discovery_document_loaded':
          console.log('Discovery document loaded');
          break;
        case 'jwks_load_error':
        case 'invalid_nonce_in_state':
        case 'token_validation_error':
          console.error('OAuth error:', event);
          this.handleAuthenticationFailure();
          break;
      }
    });
  }

  private setupPopupMessageListener(): void {
    window.addEventListener('message', (event) => {
      console.log('Popup message received:', event);

      if (event.origin !== window.location.origin) {
        console.warn('Message from invalid origin:', event.origin);
        return;
      }

      if (event.data && event.data.type === 'oauth-callback') {
        this.zone.run(async () => {
          try {
            console.log('Processing OAuth callback from popup...');

            const url = new URL(event.data.url);
            const code = url.searchParams.get('code');
            const state = url.searchParams.get('state');

            if (code && state) {
              console.log('Authorization code received:', code);

              await this.oauthService.tryLogin({
                customHashFragment: url.search.substring(1)
              });

              if (this.oauthService.hasValidAccessToken()) {
                console.log('Login successful!');
                this.handleAuthenticationSuccess();

                if (this.pendingPopup && !this.pendingPopup.closed) {
                  this.pendingPopup.close();
                }
                this.pendingPopup = null;

                this.router.navigate(['/']);
              } else {
                console.error('Login failed - no valid access token');
                this.handleLoginError('Logowanie nie powiodło się');
              }
            } else {
              console.error('No authorization code in callback URL');
              this.handleLoginError('Brak kodu autoryzacji');
            }
          } catch (error) {
            console.error('Error processing OAuth callback:', error);
            this.handleLoginError('Błąd podczas przetwarzania odpowiedzi autoryzacji');
          }
        });
      }
    });
  }

  private handleLoginError(message: string): void {
    console.error('Login error:', message);

    if (this.pendingPopup && !this.pendingPopup.closed) {
      this.pendingPopup.close();
    }
    this.pendingPopup = null;

    this.handleAuthenticationFailure();

    alert(message);
  }

  public async login(): Promise<void> {
    try {
      console.log('Starting popup login flow...');

      if (this.pendingPopup && !this.pendingPopup.closed) {
        this.pendingPopup.close();
      }

      this.pendingPopup = await this.oauthService.initLoginFlowInPopup({
        height: 600,
        width: 500
      }) as Window | null;

      console.log('Popup opened, waiting for callback...');

      const checkClosed = () => {
        if (this.pendingPopup && this.pendingPopup.closed) {
          console.log('Popup was closed by user');
          this.pendingPopup = null;
        } else if (this.pendingPopup) {
          setTimeout(checkClosed, 1000);
        }
      };
      setTimeout(checkClosed, 1000);

    } catch (error) {
      console.error('Popup login error:', error);
      this.handleLoginError('Błąd podczas otwierania okna logowania');
    }
  }

  public navigateToRegisterPage(): void {
    this.router.navigate(['/register']);
  }

  public registerApi(email: string, password: string): Observable<any> {
    const registerUrl = `${environment.authServerUrl}/api/auth/register`;
    return this.http.post(registerUrl, { email, password });
  }

  public logout(): void {
    console.log('Logging out...');
    this.oauthService.logOut();
    this.handleAuthenticationFailure();
    this.router.navigate(['/login']);
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
          name: claims.name || claims.preferred_username || claims.email,
          email: claims.email,
          preferred_username: claims.preferred_username || claims.email,
          email_verified: claims.email_verified || false,
          roles: claims.roles || []
        };
        this.userProfileSubject.next(userProfile);
        console.log('User profile updated:', userProfile);

        this.oauthService.setupAutomaticSilentRefresh();
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

  public isAuthenticated(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  public async refreshToken(): Promise<void> {
    try {
      await this.oauthService.silentRefresh();
      console.log('Token refreshed successfully');
    } catch (error) {
      console.error('Token refresh failed:', error);
      this.logout();
    }
  }
}
