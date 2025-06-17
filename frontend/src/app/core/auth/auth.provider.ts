import { inject } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './auth.config';

export function provideAuth() {
  return [
    {
      provide: 'AUTH_INIT',
      useFactory: () => {
        const oauthService = inject(OAuthService);

        return async () => {
          oauthService.configure(authConfig);

          await oauthService.loadDiscoveryDocumentAndTryLogin();

          if (oauthService.hasValidAccessToken()) {
            oauthService.setupAutomaticSilentRefresh();
          }
        };
      },
      multi: true
    }
  ];
}
