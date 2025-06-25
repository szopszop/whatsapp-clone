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
          allowedUrls: [environment.gatewayApiUrl],
          sendAccessToken: true,
        },
      })
    ),
    {
      provide: 'authAppInitializer',
      useFactory: (oauthService: OAuthService): () => Promise<void> => {
        return async () => {
          oauthService.configure(authConfig);
          await oauthService.loadDiscoveryDocumentAndTryLogin();

          if (oauthService.hasValidAccessToken()) {
            oauthService.setupAutomaticSilentRefresh();
          }
        };
      },
      deps: [OAuthService],
      multi: true,
    }
  ],
};
