import {Injectable} from '@angular/core';
import {BehaviorSubject, filter, map, Observable, throwError} from 'rxjs';
import {UserProfile} from "../models/user-profile.model";
import {environment} from "../../../environments/environment";
import {HttpErrorResponse} from "@angular/common/http";
import {jwtDecode} from "jwt-decode";
import {AuthConfig, OAuthService} from "angular-oauth2-oidc";

interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly JWT_ACCESS_TOKEN = 'access_token';
  private readonly JWT_REFRESH_TOKEN = 'refresh_token';
  private readonly API_URL = `${environment.authServerUrl}/auth`;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private userProfileSubject = new BehaviorSubject<UserProfile | null>(null);
  public userProfile$ = this.userProfileSubject.asObservable();

  public isAdmin$: Observable<boolean>;

  constructor(
      private oauthService: OAuthService,
      private authConfig: AuthConfig
  ) {
    // Konfigurujemy serwis
    this.oauthService.configure(this.authConfig);

    // Reaktywne sprawdzanie roli admina
    this.isAdmin$ = this.isAuthenticated$.pipe(
        map(isAuth => isAuth && this.hasAdminRole())
    );
  }

  public async initializeAuth(): Promise<void> {
    // Ładujemy konfigurację z discovery document i próbujemy się zalogować
    // (np. po powrocie od serwera autoryzacji)
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();

    if (this.oauthService.hasValidAccessToken()) {
      await this.handleAuthenticationSuccess();
    } else {
      this.handleAuthenticationFailure();
    }

    this.oauthService.setupAutomaticSilentRefresh();

    this.oauthService.events
        .pipe(filter(e => e.type === 'token_received'))
        .subscribe(() => this.handleAuthenticationSuccess());
  }

  public login(): void {
    this.oauthService.initCodeFlow();
  }

  public logout(): void {
    this.oauthService.logOut();
    this.handleAuthenticationFailure();
  }

  public getAccessToken(): string | null {
    return this.oauthService.getAccessToken();
  }


  private handleError(error: HttpErrorResponse) {
    console.error('API Error:', error.error);
    return throwError(() => error);
  }


  private async handleAuthenticationSuccess(): Promise<void> {
    this.isAuthenticatedSubject.next(true);
    const userProfile = (await this.oauthService.loadUserProfile()) as UserProfile;
    this.userProfileSubject.next(userProfile);
  }

  private handleAuthenticationFailure(): void {
    this.isAuthenticatedSubject.next(false);
    this.userProfileSubject.next(null);
  }


  private getRefreshToken(): string | null {
    return localStorage.getItem(this.JWT_REFRESH_TOKEN);
  }

  private setSession(authResponse: AuthResponse): void {
    localStorage.setItem(this.JWT_ACCESS_TOKEN, authResponse.accessToken);
    localStorage.setItem(this.JWT_REFRESH_TOKEN, authResponse.refreshToken);
    this.isAuthenticatedSubject.next(true);
    this.loadUserProfile();
  }

  private clearSession(): void {
    localStorage.removeItem(this.JWT_ACCESS_TOKEN);
    localStorage.removeItem(this.JWT_REFRESH_TOKEN);
    this.isAuthenticatedSubject.next(false);
    this.userProfileSubject.next(null);
  }

  private hasValidAccessToken(): boolean {
    const token = this.getAccessToken();
    if (!token) return false;
    try {
      const decoded: any = jwtDecode(token);
      return decoded.exp * 1000 > Date.now();
    } catch (error) {
      return false;
    }
  }
  private hasAdminRole(): boolean {
    const claims = this.oauthService.getIdentityClaims();
    if (claims && claims['roles']) {
      return (claims['roles'] as string[]).includes('ROLE_ADMIN');
    }
    return false;
  }

  private loadUserProfile(): void {
    const token = this.getAccessToken();
    if (token) {
      try {
        const decodedProfile = jwtDecode<UserProfile>(token);
        this.userProfileSubject.next(decodedProfile);
      } catch(error) {
        console.error('Could not decode token', error);
        this.userProfileSubject.next(null);
      }
    }
  }

}
