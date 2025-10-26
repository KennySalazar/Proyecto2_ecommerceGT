import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductosService, ProductoResponse, SpringPage, ResenaDTO } from './productos.service';
import { environment } from '../../environments/environment';
import { CarritoService } from './carrito.service';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { ReviewsService } from './reviews.service';
import { ImgUrlPipe } from '../shared/pipes/img-url.pipe';
@Component({
  standalone: true,
  selector: 'app-catalogo',
  imports: [CommonModule, FormsModule, RouterLink, ImgUrlPipe],
  templateUrl: './catalogo.component.html'
})
export class CatalogoComponent {
  private svc = inject(ProductosService);
  private carrito = inject(CarritoService);
  private router = inject(Router);
  private reviewsSvc = inject(ReviewsService);
  public Math = Math;

backendOrigin = environment.backendOrigin || 'https://interbelligerent-torie-nonstoically.ngrok-free.dev';

  productos: ProductoResponse[] = [];
  filtrados: ProductoResponse[] = [];

  filtroNombre = '';
  filtroCategoria = 'Todos';

  categorias = ['Todos', 'Tecnología', 'Hogar', 'Académico', 'Personal', 'Decoración', 'Otro'];

  ngOnInit() { this.cargar(); }

fullImg(p: any): string {
  const u: string | null = p?.imagenUrl ?? p?.imageUrl ?? null;
  if (!u) return 'assets/noimg.png';
  return u.startsWith('/uploads/') ? u : '/uploads/' + u.replace(/^\/+/, '');
}
  cargar() {
    this.productos = [];
    const tamanio = 50; 
    let pagina = 0;

    const cargarPagina = () => {
      this.svc.listarPublico(pagina, tamanio).subscribe({
        next: (page: SpringPage<ProductoResponse>) => {
          this.productos.push(...page.content);
          if (pagina + 1 < page.totalPages) {
            pagina++;
            cargarPagina();
          } else {
            this.aplicarFiltro(); 
          }
        },
        error: (e) => console.error('Error cargando productos', e)
      });
    };

    cargarPagina();
  }

fmt1(x:number){ return (Math.round((x||0)*10)/10).toFixed(1); }
estrellas(x:number){ x = x || 0; const e = Math.floor(x); return {enteras:e, vacias:5-e}; }


showResenas = false;
productoSeleccionado?: ProductoResponse;
resenas: any[] = [];
promedio = 0; totalResenas = 0; puedeComentar = false;
cargandoResenas = false;

miRating = 0;
miComentario = '';

abrirResenas(p: ProductoResponse) {
  this.productoSeleccionado = p;
  this.showResenas = true;
  this.cargarResenas(p.id);
}

cerrarResenas(){ this.showResenas = false; }

cargarResenas(id: number, sincronizar = false) {
  this.cargandoResenas = true;
  this.reviewsSvc.listar(id).subscribe({
    next: r => {
      this.promedio = r.promedio || 0;
      this.totalResenas = r.total || 0;
      this.puedeComentar = r.puedeComentar;
      this.resenas = r.items;
          if (sincronizar) {
      this.syncProductoCalificacion(id, this.promedio, this.totalResenas);
    }

      this.cargandoResenas = false;
    },
    error: _ => this.cargandoResenas = false
  });
}

seleccionarEstrella(n:number){ this.miRating = n; }

enviarResena(){
  if (!this.productoSeleccionado || this.miRating<1 || !this.miComentario.trim()) return;
  this.reviewsSvc.crear(this.productoSeleccionado.id, this.miRating, this.miComentario.trim())
    .subscribe({
      next: _ => {

        this.cargarResenas(this.productoSeleccionado!.id, true);
      
        this.miRating = 0; this.miComentario = '';
        this.cargarResenas(this.productoSeleccionado!.id);
       
        this.productoSeleccionado!.totalResenas = (this.productoSeleccionado!.totalResenas||0)+1;
        
      },
      error: e => alert(e?.error?.message || 'No se pudo enviar la reseña')
    });
}

private syncProductoCalificacion(id: number, prom: number, total: number) {
  const upd = (x: any) => x.id === id ? { ...x, ratingPromedio: prom, totalResenas: total } : x;
 
  this.productos = this.productos.map(upd);
  this.filtrados = this.filtrados.map(upd);
}

  aplicarFiltro() {
    const nombre = this.filtroNombre.toLowerCase();
    const categoria = this.filtroCategoria;
    this.filtrados = this.productos.filter(p => {
      const matchNombre = p.nombre.toLowerCase().includes(nombre);
      const matchCategoria = categoria === 'Todos' || p.categoria === categoria;
      return matchNombre && matchCategoria;
    });
  }

  addToCart(id: number) {
    this.carrito.agregar(id, 1).subscribe({
      next: _ => this.router.navigateByUrl('/carrito'),
      error: err => alert('No se pudo agregar al carrito')
    });
  }

  asQ(q: number) { return q.toLocaleString('es-GT', { style: 'currency', currency: 'GTQ' }); }
}