import {Routes} from '@angular/router';
import {AdminPanelComponent} from './features/admin/admin-panel.component';
import {authGuard} from './core/auth/guards/auth.guard';
import {adminGuard} from './core/auth/guards/admin.guard';
import {LayoutComponent} from './features/layout/layout.component';
import {PageNotFoundComponent} from './features/page-not-found/page-not-found.component';
import {ChatComponent} from './features/chat/chat.component';
import {HomeComponent} from './features/home/home.component';

export const routes: Routes = [
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
