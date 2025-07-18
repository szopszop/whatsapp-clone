import {AuthConfig} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.authServerUrl,
  redirectUri: window.location.origin,
  clientId: 'oidc-client',
  responseType: 'code',
  scope: 'openid profile api.read',
  showDebugInformation: !environment.production,
  //requireHttps: environment.production,
  silentRefreshRedirectUri: window.location.origin + '/silent-refresh.html',
  silentRefreshTimeout: 5000,
  clearHashAfterLogin: false,
  silentRefreshShowIFrame: false,
};
