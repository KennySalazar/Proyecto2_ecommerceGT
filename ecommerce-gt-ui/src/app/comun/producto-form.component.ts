import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductosService } from './productos.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-producto-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './producto-form.component.html'
})
export class ProductoFormComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private svc = inject(ProductosService);
  private http = inject(HttpClient);

  id?: number;
  titulo = 'Nuevo producto';
  cargando = false;
  preview?: string;
  categorias: {id:number; nombre:string}[] = [];

  form = {
    nombre: '',
    descripcion: '',
    precioCents: 0,
    stock: 1,
    estadoArticulo: 'NUEVO' as 'NUEVO'|'USADO',
    categoriaId: 1
  };
  imagenFile?: File;

  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');

  // SE EJECUTA AL INICIAR EL COMPONENTE: CARGA DATOS Y CATEGORÍAS
  ngOnInit(){
    this.id = Number(this.route.snapshot.paramMap.get('id')) || undefined;
    if (this.id) this.titulo = 'Editar producto';

    // CARGA LISTA DE CATEGORÍAS DESDE EL BACKEND O USA VALORES POR DEFECTO
    this.http.get<any[]>(`${environment.apiBase}/catalogos/categorias`).subscribe({
      next: res => this.categorias = res,
      error: _ => this.categorias = [
        {id:1,nombre:'Tecnología'},{id:2,nombre:'Hogar'},
        {id:3,nombre:'Académico'},{id:4,nombre:'Personal'},
        {id:5,nombre:'Decoración'},{id:6,nombre:'Otro'},
      ]
    });

    // SI ES EDICIÓN, CARGA LOS DATOS DEL PRODUCTO EXISTENTE
    if (this.id) {
      this.svc.obtenerMio(this.id).subscribe(p => {
        this.form = {
          nombre: p.nombre,
          descripcion: p.descripcion,
          precioCents: p.precioCents,
          stock: p.stock,
          estadoArticulo: p.estadoArticulo,
          categoriaId: (p as any).categoriaId ?? this.form.categoriaId
        };

        const rel = (p as any).imagenUrl ?? (p as any).imageUrl ?? null;
        this.preview = rel ? (this.backendOrigin + rel) : undefined;
      });
    }
  }

  // CARGA UNA IMAGEN Y MUESTRA PREVISUALIZACIÓN EN EL FORMULARIO
  onFile(e: any){
    const f: File = e.target.files?.[0];
    if (!f) return;
    this.imagenFile = f;
    const r = new FileReader();
    r.onload = () => this.preview = r.result as string;
    r.readAsDataURL(f);
  }

  // GUARDA O ACTUALIZA EL PRODUCTO SEGÚN SI EXISTE ID
  guardar(){
    this.cargando = true;

    // VALIDACIÓN DE CAMPOS OBLIGATORIOS
    if (!this.form.nombre || !this.form.descripcion || this.form.precioCents<=0 || this.form.stock<1) {
      this.cargando=false; alert('Completa los campos obligatorios'); return;
    }

    // DETERMINA SI ES CREACIÓN O ACTUALIZACIÓN
    const op$ = this.id
      ? this.svc.actualizar(this.id, this.form, this.imagenFile)
      : this.svc.crear(this.form, this.imagenFile);

    // EJECUTA LA OPERACIÓN Y MANEJA RESPUESTA
    op$.subscribe({
      next: _ => {
        this.cargando=false;
        this.router.navigateByUrl('/mis-productos');
      },
      error: err => {
        this.cargando=false;
        alert(err?.error?.message ?? 'Error al guardar');
      }
    });
  }
}
