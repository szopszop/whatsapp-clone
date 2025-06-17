import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['../auth.styles.scss']
})
export class LoginComponent {
  constructor(private authService: AuthService) {
  }

  login() {
    this.authService.login();
  }
}
