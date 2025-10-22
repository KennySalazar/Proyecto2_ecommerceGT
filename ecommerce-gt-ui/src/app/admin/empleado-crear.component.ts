import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { EmpleadosService } from './empleados.service';

@Component({
  selector: 'app-empleado-crear',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './empleado-crear.component.html'
})
export class EmpleadoCrearComponent {
  private svc = inject(EmpleadosService);
  private router = inject(Router);

  nombre = '';
  correo = '';
  telefono = '';
  contrasena = '';  
  rolCodigo: 'ADMIN'|'MODERADOR'|'LOGISTICA' = 'MODERADOR';
  error = '';
  loading = false;

  enviar(f: NgForm) {
    if (f.invalid) return;
    this.loading = true;

    this.svc.crear({
      nombre: this.nombre,
      correo: this.correo,
      telefono: this.telefono,
      contrasena: this.contrasena,  
      rolCodigo: this.rolCodigo
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl('/admin/empleados');
      },
      error: (e) => {
        this.loading = false;
        this.error = e?.error?.message ?? 'No se pudo crear';
      }
    });
  }
}
