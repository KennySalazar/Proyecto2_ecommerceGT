import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

// COMPONENTE PARA EL INICIO DE SESIÓN DE LOS USUARIOS
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  // VARIABLES DEL FORMULARIO
  correo = '';
  password = '';
  loading = false;
  error = '';

  private auth = inject(AuthService);
  private router = inject(Router);

  // FUNCIÓN QUE SE EJECUTA AL ENVIAR EL FORMULARIO
  onSubmit(f: NgForm) {
    // SI EL FORMULARIO ES INVÁLIDO, NO HACE NADA
    if (f.invalid) return;
    this.loading = true;
    this.error = '';

    // LLAMA AL SERVICIO DE AUTENTICACIÓN PARA INICIAR SESIÓN
    this.auth.login({ correo: this.correo, password: this.password }).subscribe({
      next: (resp) => {
        this.loading = false;

        // GUARDA LA SESIÓN EN LOCALSTORAGE
        this.auth.guardarSesion(resp); 

        // OBTIENE EL ROL DEL USUARIO
        const rol = resp.rolCodigo ?? resp.rol ?? 'COMUN';
        console.log('Rol del usuario:', rol);

        // REDIRIGE SEGÚN EL ROL DEL USUARIO
        switch (rol) {
          case 'ADMIN':
            this.router.navigateByUrl('/admin/empleados'); 
            break;
          case 'MODERADOR':
            this.router.navigateByUrl('/moderador/solicitudes'); 
            break;
          case 'LOGISTICA':
            this.router.navigateByUrl('/logistica/pendientes'); 
            break;
          default:
            this.router.navigateByUrl('/inicio'); 
            break;
        }
      },
      error: (err) => {
        // MUESTRA MENSAJE DE ERROR SI LAS CREDENCIALES SON INCORRECTAS
        this.loading = false;
        this.error = err?.error?.message ?? 'Credenciales inválidas';
      }
    });
  }

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit() {
    // LIMPIA CUALQUIER TOKEN ANTERIOR AL CARGAR EL LOGIN
    localStorage.removeItem('token');
  }
}
