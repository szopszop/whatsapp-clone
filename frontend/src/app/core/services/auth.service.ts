import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authCodeFlowConfig } from '../../auth/auth.config';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$: Observable<boolean> = this.isAuthenticatedSubject.asObservable();

  private userProfileSubject = new BehaviorSubject<any>(null);
  public userProfile$: Observable<any> = this.userProfileSubject.asObservable();

  constructor(private oauthService: OAuthService) {
    this.oauthService.configure(authCodeFlowConfig);
  }

  public async initializeAuth(): Promise<void> {
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();

    if (this.oauthService.hasValidAccessToken()) {
      this.isAuthenticatedSubject.next(true);
      const userProfile = await this.oauthService.loadUserProfile();
      this.userProfileSubject.next(userProfile);
    } else {
      this.isAuthenticatedSubject.next(false);
      this.userProfileSubject.next(null);
    }

    this.oauthService.setupAutomaticSilentRefresh();

    this.oauthService.events
      .pipe(filter(e => e.type === 'token_received'))
      .subscribe(async _ => {
        this.isAuthenticatedSubject.next(true);
        const userProfile = await this.oauthService.loadUserProfile();
        this.userProfileSubject.next(userProfile);
      });
  }

  public login(): void {
    this.oauthService.initCodeFlow();
  }

  public logout(): void {
    this.oauthService.logOut();
    this.isAuthenticatedSubject.next(false);
    this.userProfileSubject.next(null);
  }

  public get accessToken(): string {
    return this.oauthService.getAccessToken();
  }

  public getRoles(): string[] {
    const claims = this.oauthService.getIdentityClaims();
    if (claims && claims['roles']) {
      return claims['roles'];
    }
    return [];
  }

  public isAdmin(): boolean {
    return this.getRoles().includes('ROLE_ADMIN');
  }
}
