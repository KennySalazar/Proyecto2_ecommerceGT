import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {
  nombre = '';
  correo = '';
  telefono = '';
  password = '';
  confirmar = '';
  loading = false;
  error = '';

  private auth = inject(AuthService);
  private router = inject(Router);

  onSubmit(f: NgForm) {
    if (f.invalid) return;
    if (this.password !== this.confirmar) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }
    this.loading = true;
    this.error = '';

    this.auth.register({
      nombre: this.nombre,
      correo: this.correo,
      telefono: this.telefono,
      password: this.password
    }).subscribe({
      next: _ => {
        this.loading = false;
        alert('¡Cuenta creada! Ahora puedes iniciar sesión.');
        this.router.navigateByUrl('/login');
      },
      error: err => {
        this.loading = false;
        this.error = err?.error?.message ?? 'No se pudo registrar';
      }
    });
  }
}
