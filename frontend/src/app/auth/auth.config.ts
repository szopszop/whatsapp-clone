import {environment} from "../../environments/environment";
import { AuthConfig } from 'angular-oauth2-oidc';

export const authCodeFlowConfig: AuthConfig = {
    issuer: environment.authServerUrl,
    redirectUri: window.location.origin + '/index.html',
    clientId: 'client',
    responseType: 'code',
    scope: 'openid profile email',
    usePkce: true,
    showDebugInformation: !environment.production,
    disableNonceCheck: true
};
