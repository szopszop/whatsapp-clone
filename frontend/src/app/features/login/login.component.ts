import { Component } from '@angular/core';
import {AuthService} from '../../core/services/auth.service';
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  constructor(private authService: AuthService) {
  }

  login() {
    this.authService.login();
  }
}
