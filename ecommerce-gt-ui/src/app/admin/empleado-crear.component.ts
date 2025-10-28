import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { EmpleadosService } from './empleados.service';
import { RouterLink } from '@angular/router';

// COMPONENTE PARA CREAR O EDITAR EMPLEADOS
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

  // VARIABLES DEL FORMULARIO
  nombre = '';
  correo = '';
  telefono = '';
  rolCodigo: 'ADMIN' | 'MODERADOR' | 'LOGISTICA' = 'MODERADOR';
  contrasena = '';

  // VARIABLES DE ESTADO
  loading = false;
  error = '';
  editId?: number;
  rolBloqueado = false;

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit() {
    // SE VERIFICA SI EXISTE UN ID PARA EDITAR
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editId = +idParam;
      this.cargarEmpleado(this.editId);
    }
  }

  // CARGA LOS DATOS DE UN EMPLEADO EXISTENTE
  private cargarEmpleado(id: number) {
    this.loading = true;
    this.api.get(id).subscribe({
      next: (e) => {
        // ASIGNA LOS VALORES RECIBIDOS DEL SERVICIO
        this.nombre = e.nombre ?? '';
        this.correo = e.correo ?? '';
        this.telefono = e.telefono ?? '';
        this.rolCodigo = e.rol as any;
        this.rolCodigo = (e.rol as any) ?? 'COMUN';
        // BLOQUEA EL CAMBIO DE ROL SI ES COMÚN
        this.rolBloqueado = (e.rol === 'COMUN'); 
        this.loading = false;
      },
      error: (_) => {
        this.loading = false;
        this.error = 'No se pudo cargar el empleado';
      },
    });
  }

  // ENVÍA LOS DATOS DEL FORMULARIO (CREAR O EDITAR)
  enviar(f: NgForm) {
    if (f.invalid) return;
    this.loading = true;
    this.error = '';

    // SE PREPARA EL OBJETO A ENVIAR
    const body: any = {
      nombre: this.nombre.trim(),
      correo: this.correo.trim(),
      telefono: this.telefono?.trim(),
    };

    // SOLO AGREGA EL ROL SI NO ESTÁ BLOQUEADO
    if (!this.rolBloqueado) {
      body.rolCodigo = this.rolCodigo;
    }

    // SOLO AGREGA CONTRASEÑA SI ES NUEVO EMPLEADO
    if (!this.editId) {
      body.contrasena = this.contrasena;
    }

    // SE DEFINE SI ES CREACIÓN O ACTUALIZACIÓN
    const obs = this.editId
      ? this.api.actualizar(this.editId, body)
      : this.api.crear(body);

    // SE SUSCRIBE AL RESULTADO DE LA PETICIÓN
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

  // RETORNA TRUE SI SE ESTÁ EDITANDO
  get esEdicion() {
    return !!this.editId;
  }
}
