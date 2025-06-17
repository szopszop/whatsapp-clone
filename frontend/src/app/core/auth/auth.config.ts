import { AuthConfig } from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.authServerUrl,
  redirectUri: window.location.origin,
  clientId: 'angular-client',
  responseType: 'code',
  scope: 'openid profile email',
  showDebugInformation: !environment.production,
  requireHttps: environment.production,
  silentRefreshRedirectUri: window.location.origin + '/silent-refresh.html',
  silentRefreshTimeout: 5000,
  clearHashAfterLogin: true,
  silentRefreshShowIFrame: false,
};
