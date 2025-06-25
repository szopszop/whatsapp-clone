import {AuthConfig} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.authServerUrl,
  redirectUri: window.location.origin + '/callback.html',
  clientId: 'oidc-client',
  responseType: 'code',
  scope: 'openid profile api.read',
  showDebugInformation: !environment.production,
  //requireHttps: !environment.production,

  silentRefreshRedirectUri: window.location.origin + '/silent-refresh.html',
  useSilentRefresh: true,
  sessionChecksEnabled: true,
  silentRefreshTimeout: 5000,
  clearHashAfterLogin: true,
  silentRefreshShowIFrame: false,
  postLogoutRedirectUri: window.location.origin,
  strictDiscoveryDocumentValidation: false,

  timeoutFactor: 0.8,
  skipSubjectCheck: true
};
