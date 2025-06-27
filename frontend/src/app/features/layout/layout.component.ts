import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AsyncPipe, NgIf} from '@angular/common';
import {Observable} from 'rxjs';
import {AuthService} from '../../core/services/auth.service';
import {UserProfile} from '../../core/models/user-profile.model';
@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIf, AsyncPipe],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss', '../../styles/_auth.scss', "../../styles/styles.scss"],
})
export class LayoutComponent {
  isAuthenticated$: Observable<boolean>;
  userProfile$: Observable<UserProfile | null>;
  isAdmin$: Observable<boolean>;

  constructor(private authService: AuthService) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
    this.userProfile$ = this.authService.userProfile$;
    this.isAdmin$ = this.authService.isAdmin$;
  }

  logout(): void {
    this.authService.logout();
  }

  login() {
    this.authService.login()
  }

  register() {
    this.authService.navigateToRegisterPage();
  }
}
