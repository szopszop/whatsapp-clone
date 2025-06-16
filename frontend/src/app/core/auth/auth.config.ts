import {environment} from "../../../environments/environment";
import { AuthConfig } from 'angular-oauth2-oidc';
import {APP_INITIALIZER, EnvironmentProviders, Provider} from '@angular/core';
import {AuthService} from '../services/auth.service';

export const authCodeFlowConfig: AuthConfig = {
  issuer: environment.authServerUrl,
  redirectUri: window.location.origin,
  clientId: 'angular-ui',
  responseType: 'code',
  scope: 'openid profile email',
  showDebugInformation: !environment.production,
};

export function initializeAuth(authService: AuthService): () => Promise<void> {
  return () => authService.initializeAuth();
}


export function provideAuth(): (Provider | EnvironmentProviders)[] {
  return [
    AuthService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      deps: [AuthService],
      multi: true,
    },
  ];
}
