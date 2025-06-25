import {ApplicationConfig, provideAppInitializer, inject} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors, withXsrfConfiguration } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { OAuthModule, OAuthService } from 'angular-oauth2-oidc';

import { routes } from './app.routes';

import { environment } from '../environments/environment';
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
    provideAppInitializer(() => {
      const oauthService = inject(OAuthService);

      return (async () => {
        oauthService.configure(authConfig);
        await oauthService.loadDiscoveryDocumentAndTryLogin();

        if (oauthService.hasValidAccessToken()) {
          oauthService.setupAutomaticSilentRefresh();
        }
      })();
    })
  ],
};
