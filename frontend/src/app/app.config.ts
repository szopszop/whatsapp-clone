import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors, withXsrfConfiguration} from '@angular/common/http';
import {OAuthModule} from 'angular-oauth2-oidc';

import {routes} from './app.routes';
import {environment} from '../environments/environment';
import {authInterceptor} from './core/interceptors/auth.interceptor';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {provideAuth} from './core/auth/auth.config';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([authInterceptor]),
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN',
      })),

    importProvidersFrom(
      OAuthModule.forRoot({
        resourceServer: {
          allowedUrls: [environment.gatewayApiUrl],
          sendAccessToken: true,
        },
      })
    ),
    ...provideAuth(),
  ],
};
