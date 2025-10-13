import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule,RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  correo = '';
  password = '';
  loading = false;
  error = '';

  private auth = inject(AuthService);

  onSubmit(f: any) {
    if (f.invalid) return;
    this.loading = true;
    this.error = '';

    this.auth.login({ correo: this.correo, password: this.password }).subscribe({
      next: (resp) => {
        this.loading = false;
        localStorage.setItem('token', resp.token ?? '');
        alert(`Bienvenido ${resp.nombre ?? ''}`);
        
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message ?? 'Credenciales inválidas';
      }
    });
  }
}
