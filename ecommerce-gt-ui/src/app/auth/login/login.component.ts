import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  correo = '';
  password = '';
  loading = false;
  error = '';

  private auth = inject(AuthService);
  private router = inject(Router);

  onSubmit(f: NgForm) {
    if (f.invalid) return;
    this.loading = true;
    this.error = '';

    this.auth.login({ correo: this.correo, password: this.password }).subscribe({
      next: (resp) => {
        this.loading = false;
        this.auth.guardarSesion(resp); 

        
        const rol = resp.rolCodigo ?? resp.rol ?? 'COMUN';
        console.log('Rol del usuario:', rol);
        switch (rol) {
          case 'ADMIN':
            this.router.navigateByUrl('/admin/empleados'); break;
          case 'MODERADOR':
            this.router.navigateByUrl('/moderador/solicitudes'); break;
          case 'LOGISTICA':
            this.router.navigateByUrl('/logistica/pendientes'); break;
          default:
            this.router.navigateByUrl('/inicio'); break;
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message ?? 'Credenciales inválidas';
      }
    });
  }
  ngOnInit() {
 
  localStorage.removeItem('token');
}
}
