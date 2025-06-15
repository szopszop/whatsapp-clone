import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { OAuthModule } from 'angular-oauth2-oidc';

import { AuthService } from './core/services/auth.service';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { AppRoutingModule } from './app-routing.module';
import { ChatModule } from './chat/chat.module';
import { AdminModule } from './admin/admin.module';
import { environment } from '../environments/environment';

// Funkcja inicjalizacyjna - zostaje bez zmian
export function initializeApp(authService: AuthService) {
  return (): Promise<any> => {
    return authService.initializeAuth();
  };
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    PageNotFoundComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: [environment.gatewayApiUrl],
        sendAccessToken: true,
      },
    }),
    ChatModule,
    AdminModule
  ],
  providers: [
    // Nowy sposób na HttpClient, ale zachowuje kompatybilność ze starymi interceptorami
    provideHttpClient(withInterceptorsFromDi()),

    AuthService,

    // APP_INITIALIZER zostaje bez zmian
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [AuthService],
      multi: true,
    },

    // Stary interceptor pozostaje bez zmian
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
