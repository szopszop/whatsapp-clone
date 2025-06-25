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
          allowedUrls: [environment.gatewayApiUrl, environment.authServerUrl],
          sendAccessToken: true,
        },
      })
    ),
    {
      provide: 'authAppInitializer',
      useFactory: (oauthService: OAuthService): (() => Promise<void>) => {
        return async (): Promise<void> => {
          try {
            console.log('Initializing OAuth service...');

            oauthService.configure(authConfig);

            await oauthService.loadDiscoveryDocument();
            console.log('Discovery document loaded');

            await oauthService.tryLogin({
              onTokenReceived: (info) => {
                console.log('Token received during initialization:', info);
              }
            });

            if (oauthService.hasValidAccessToken()) {
              console.log('Valid access token found, setting up automatic refresh');
              oauthService.setupAutomaticSilentRefresh();
            } else {
              console.log('No valid access token found');
            }

          } catch (error) {
            console.error('Error during OAuth initialization:', error);
          }
        };
      },
      deps: [OAuthService],
      multi: true,
    },
  ],
};
