import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {OAuthService} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private oauthService: OAuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const apiUrl = environment.gatewayApiUrl;

    if (req.url.startsWith(apiUrl) && this.oauthService.hasValidAccessToken()) {
      const token = this.oauthService.getAccessToken();
      const headers = req.headers.set('Authorization', `Bearer ${token}`);
      const authReq = req.clone({headers});
      return next.handle(authReq);
    }

    return next.handle(req);
  }
}
