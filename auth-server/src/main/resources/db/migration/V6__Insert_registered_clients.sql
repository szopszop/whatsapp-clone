INSERT INTO oauth2_registered_client (
    id,
    client_id,
    client_id_issued_at,
    client_secret,
    client_secret_expires_at,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    redirect_uris,
    post_logout_redirect_uris,
    scopes,
    client_settings,
    token_settings
) VALUES (
             gen_random_uuid(),
             'oidc-client',
             CURRENT_TIMESTAMP,
             NULL,
             NULL,
             'Angular SPA Client',
             'none',
             'authorization_code,refresh_token',
             'http://localhost:4200,http://localhost:4200/silent-refresh.html',
             'http://localhost:4200',
             'openid,profile,api.read',
             '{
                 "@class": "java.util.HashMap",
                 "requireProofKey": true,
                 "requireAuthorizationConsent": false
              }',
             '{
                 "@class": "java.util.HashMap",
                 "accessTokenTimeToLive": 900,
                 "refreshTokenTimeToLive": 86400,
                 "reuseRefreshTokens": true
              }'
         );

INSERT INTO oauth2_registered_client (
    id,
    client_id,
    client_id_issued_at,
    client_secret,
    client_secret_expires_at,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    redirect_uris,
    post_logout_redirect_uris,
    scopes,
    client_settings,
    token_settings
) VALUES (
             gen_random_uuid(),
             'auth-server-internal',
             CURRENT_TIMESTAMP,
             '${auth_server_internal_secret}',
             NULL,
             'Auth Server Internal Client',
             'client_secret_basic',
             'client_credentials',
             NULL,
             NULL,
             'internal.user.read',
             '{
                 "@class": "java.util.HashMap"
             }',
             '{
                 "@class": "java.util.HashMap",
                 "accessTokenTimeToLive": 3600
             }'
         );