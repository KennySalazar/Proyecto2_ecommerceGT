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

  ngOnInit(){
    this.id = Number(this.route.snapshot.paramMap.get('id')) || undefined;
    if (this.id) this.titulo = 'Editar producto';

    this.http.get<any[]>(`${environment.apiBase}/catalogos/categorias`).subscribe({
      next: res => this.categorias = res,
      error: _ => this.categorias = [
        {id:1,nombre:'Tecnología'},{id:2,nombre:'Hogar'},
        {id:3,nombre:'Académico'},{id:4,nombre:'Personal'},
        {id:5,nombre:'Decoración'},{id:6,nombre:'Otro'},
      ]
    });
  }

  onFile(e: any){
    const f: File = e.target.files?.[0];
    if (!f) return;
    this.imagenFile = f;
    const r = new FileReader();
    r.onload = () => this.preview = r.result as string;
    r.readAsDataURL(f);
  }

  guardar(){
    this.cargando = true;

   
    if (!this.form.nombre || !this.form.descripcion || this.form.precioCents<=0 || this.form.stock<1) {
      this.cargando=false; alert('Completa los campos obligatorios'); return;
    }

    const op$ = this.id
      ? this.svc.actualizar(this.id, this.form, this.imagenFile)
      : this.svc.crear(this.form, this.imagenFile);

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
