import {Routes} from '@angular/router';
import {LoginComponent} from './features/auth/login/login.component';
import {AdminPanelComponent} from './features/admin/admin-panel.component';
import {authGuard} from './core/auth/guards/auth.guard';
import {adminGuard} from './core/auth/guards/admin.guard';
import {LayoutComponent} from './features/layout/layout.component';
import {PageNotFoundComponent} from './features/page-not-found/page-not-found.component';
import {ChatComponent} from './features/chat/chat.component';
import {RegisterComponent} from './features/auth/register/register.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'chat', pathMatch: 'full' },
      { path: 'chat', component: ChatComponent },
      {
        path: 'admin',
        component: AdminPanelComponent,
        canActivate: [adminGuard]
      },
    ],
  },
  // 404
  { path: '**', component: PageNotFoundComponent },
]
