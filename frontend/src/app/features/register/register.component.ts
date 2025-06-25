import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../core/services/auth.service';
import {RegisterRequest} from '../../core/models/register-request.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  registrationError: string | null = null;
  isLoading = false;
  showPassword = false;

  passwordRequirements = [
    { label: 'Minimum 8 znaków', check: (pwd: string) => pwd.length >= 8 },
    { label: 'Jedna wielka litera', check: (pwd: string) => /[A-Z]/.test(pwd) },
    { label: 'Jedna mała litera', check: (pwd: string) => /[a-z]/.test(pwd) },
    { label: 'Jedna cyfra', check: (pwd: string) => /[0-9]/.test(pwd) },
    { label: 'Znak specjalny', check: (pwd: string) => /[@#$%^&+=!]/.test(pwd) }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.pattern(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,100}$/)
      ]]
    });
  }

  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }

  getPasswordRequirementStatus(requirement: any): boolean {
    const password = this.password?.value || '';
    return requirement.check(password);
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    if (this.registerForm.valid && !this.isLoading) {
      this.isLoading = true;
      this.registrationError = null;

      const { email, password } = this.registerForm.value;

      this.authService.registerApi({email, password} as RegisterRequest).subscribe({
        next: () => {
          this.router.navigate(['/login'], {
            queryParams: { registered: 'true' }
          });
        },
        error: (err) => {
          this.isLoading = false;
          if (err.status === 409) {
            this.registrationError = 'Użytkownik o tym adresie email już istnieje.';
          } else if (err.status === 400) {
            this.registrationError = 'Nieprawidłowe dane. Sprawdź wymagania dotyczące hasła.';
          } else {
            this.registrationError = 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie.';
          }
        }
      });
    }
  }

  loginWithGoogle() {
    this.authService.login();
  }

  navigateToLogin() {
    this.authService.login();
  }
}
