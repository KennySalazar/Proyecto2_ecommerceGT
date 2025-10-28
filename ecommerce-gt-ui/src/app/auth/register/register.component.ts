import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

// COMPONENTE PARA REGISTRAR NUEVOS USUARIOS
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {
  // VARIABLES DEL FORMULARIO
  nombre = '';
  correo = '';
  telefono = '';
  password = '';
  confirmar = '';
  loading = false;
  error = '';

  private auth = inject(AuthService);
  private router = inject(Router);

  // FUNCIÓN QUE SE EJECUTA AL ENVIAR EL FORMULARIO
  onSubmit(f: NgForm) {
    // SI EL FORMULARIO ES INVÁLIDO, NO CONTINÚA
    if (f.invalid) return;

    // VERIFICA QUE LAS CONTRASEÑAS COINCIDAN
    if (this.password !== this.confirmar) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }

    this.loading = true;
    this.error = '';

    // LLAMA AL SERVICIO DE AUTENTICACIÓN PARA REGISTRAR UN NUEVO USUARIO
    this.auth.register({
      nombre: this.nombre,
      correo: this.correo,
      telefono: this.telefono,
      password: this.password
    }).subscribe({
      // SI SE REGISTRA CORRECTAMENTE, MUESTRA MENSAJE Y REDIRIGE AL LOGIN
      next: _ => {
        this.loading = false;
        alert('¡Cuenta creada! Ahora puedes iniciar sesión.');
        this.router.navigateByUrl('/login');
      },
      // SI OCURRE UN ERROR, MUESTRA MENSAJE EN PANTALLA
      error: err => {
        this.loading = false;
        this.error = err?.error?.message ?? 'No se pudo registrar';
      }
    });
  }
}
