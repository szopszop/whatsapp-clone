import {ApplicationConfig, importProvidersFrom, inject, provideAppInitializer} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {provideHttpClient, withInterceptors, withXsrfConfiguration} from '@angular/common/http';
import {OAuthModule, OAuthService} from 'angular-oauth2-oidc';

import {routes} from './app.routes';

import {environment} from '../environments/environment';
import {authConfig} from './core/auth/auth.config';
import {authInterceptor} from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(
      withInterceptors([authInterceptor]),
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN',
      })
    ),
    importProvidersFrom(
      OAuthModule.forRoot({
        resourceServer: {
          allowedUrls: [environment.gatewayApiUrl, environment.authServerUrl, environment.grafanaUrl],
          sendAccessToken: true,
        },
      })
    ),
    provideAppInitializer(() => {
      const oauthService = inject(OAuthService);

      return (async () => {
        oauthService.configure(authConfig);

        try {
          await oauthService.loadDiscoveryDocumentAndTryLogin();

          if (oauthService.hasValidAccessToken()) {
            oauthService.setupAutomaticSilentRefresh();
          }

          console.log('OAuth initialization completed, has valid token:', oauthService.hasValidAccessToken());
        } catch (error) {
          console.error('OAuth initialization failed:', error);
        }
      })();
    })
  ],
};
