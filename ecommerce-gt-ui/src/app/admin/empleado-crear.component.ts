import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { EmpleadosService } from './empleados.service';
import { RouterLink } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-empleado-crear',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './empleado-crear.component.html'
})
export class EmpleadoCrearComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private api = inject(EmpleadosService);

  nombre = '';
  correo = '';
  telefono = '';
  rolCodigo: 'ADMIN' | 'MODERADOR' | 'LOGISTICA' = 'MODERADOR';
  contrasena = '';

  loading = false;
  error = '';
  editId?: number;

   // 🔒 Si es COMUN, el rol queda bloqueado y no editable
  rolBloqueado = false;

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editId = +idParam;
      this.cargarEmpleado(this.editId);
    }
  }

  private cargarEmpleado(id: number) {
    this.loading = true;
    this.api.get(id).subscribe({
      next: (e) => {
        this.nombre = e.nombre ?? '';
        this.correo = e.correo ?? '';
        this.telefono = e.telefono ?? '';
        this.rolCodigo = e.rol as any;
        this.rolCodigo = (e.rol as any) ?? 'COMUN';
        this.rolBloqueado = (e.rol === 'COMUN'); 
        this.loading = false;
      },
      error: (_) => {
        this.loading = false;
        this.error = 'No se pudo cargar el empleado';
      },
    });
  }

  enviar(f: NgForm) {
    if (f.invalid) return;
    this.loading = true;
    this.error = '';

    const body: any = {
      nombre: this.nombre.trim(),
      correo: this.correo.trim(),
      telefono: this.telefono?.trim(),
    };

        if (!this.rolBloqueado) {
      body.rolCodigo = this.rolCodigo;
    }

    // Solo incluir contraseña si es creación
    if (!this.editId) {
      body.contrasena = this.contrasena;
    }

    const obs = this.editId
      ? this.api.actualizar(this.editId, body)
      : this.api.crear(body);

    obs.subscribe({
      next: (_) => {
        this.loading = false;
        this.router.navigateByUrl('/admin/empleados');
      },
      error: (_) => {
        this.loading = false;
        this.error = 'No se pudo guardar';
      },
    });
  }

  get esEdicion() {
    return !!this.editId;
  }
}
