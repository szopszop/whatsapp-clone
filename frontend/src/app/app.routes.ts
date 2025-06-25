import {Routes} from '@angular/router';
import {LoginComponent} from './features/login/login.component';
import {AdminPanelComponent} from './features/admin/admin-panel.component';
import {authGuard} from './core/auth/guards/auth.guard';
import {adminGuard} from './core/auth/guards/admin.guard';
import {LayoutComponent} from './features/layout/layout.component';
import {PageNotFoundComponent} from './features/page-not-found/page-not-found.component';
import {ChatComponent} from './features/chat/chat.component';
import {HomeComponent} from './features/home/home.component';
import {RegisterComponent} from './features/register/register.component';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {
    path: '',
    component: LayoutComponent,
    children: [
      {path: '', component: HomeComponent},
      {path: 'chat', component: ChatComponent, canActivate: [authGuard]},
      {path: 'admin', component: AdminPanelComponent, canActivate: [adminGuard]},
    ]
  },
  // 404
  {path: '**', component: PageNotFoundComponent},
]
