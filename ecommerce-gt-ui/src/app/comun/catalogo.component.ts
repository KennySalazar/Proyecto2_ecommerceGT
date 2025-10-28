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
  
  private svc = inject(ProductosService);     // SERVICIO PARA LISTAR PRODUCTOS PÚBLICOS
  private carrito = inject(CarritoService);   // SERVICIO PARA MANEJAR CARRITO
  private router = inject(Router);            // NAVEGACIÓN
  private reviewsSvc = inject(ReviewsService);// SERVICIO DE RESEÑAS
  public Math = Math;                         // EXPONER MATH A LA PLANTILLA

  // ORIGEN DEL BACKEND (FALLBACK A NGROK)
  backendOrigin = environment.backendOrigin || 'https://interbelligerent-torie-nonstoically.ngrok-free.dev';

  // ESTADO PRINCIPAL DEL CATÁLOGO
  productos: ProductoResponse[] = []; 
  filtrados: ProductoResponse[] = []; 

  // FILTROS DE BÚSQUEDA
  filtroNombre = '';
  filtroCategoria = 'Todos';
  categorias = ['Todos', 'Tecnología', 'Hogar', 'Académico', 'Personal', 'Decoración', 'Otro'];

  ngOnInit() { this.cargar(); } // AL INICIAR, CARGA EL CATÁLOGO

  // RESUELVE URL COMPLETA DE IMAGEN O DEVUELVE IMAGEN POR DEFECTO
  fullImg(p: any): string {
    const u: string | null = p?.imagenUrl ?? p?.imageUrl ?? null;
    if (!u) return 'assets/noimg.png';
    return u.startsWith('/uploads/') ? u : '/uploads/' + u.replace(/^\/+/, '');
  }

  // CARGA PRODUCTOS PÚBLICOS EN FORMA PAGINADA, ACUMULANDO RESULTADOS
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

  // FORMATEO NUMÉRICO A 1 DECIMAL COMO STRING
  fmt1(x:number){ return (Math.round((x||0)*10)/10).toFixed(1); }

  // CALCULA ESTRELLAS ENTERAS Y VACÍAS PARA UI (DE 0 A 5)
  estrellas(x:number){ x = x || 0; const e = Math.floor(x); return {enteras:e, vacias:5-e}; }

  // ESTADO DE RESEÑAS/COMENTARIOS
  showResenas = false;                        
  productoSeleccionado?: ProductoResponse;    
  resenas: any[] = [];                       
  promedio = 0; totalResenas = 0; puedeComentar = false; 
  cargandoResenas = false;                    

  // ESTADO DEL FORM DE NUEVA RESEÑA
  miRating = 0;
  miComentario = '';

  // ABRE PANEL DE RESEÑAS Y CARGA DATOS
  abrirResenas(p: ProductoResponse) {
    this.productoSeleccionado = p;
    this.showResenas = true;
    this.cargarResenas(p.id);
  }

  // CIERRA PANEL DE RESEÑAS
  cerrarResenas(){ this.showResenas = false; }

  // OBTIENE RESEÑAS DEL PRODUCTO; OPCIONALMENTE SINCRONIZA RATING EN LISTAS
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

  // SELECCIÓN DE ESTRELLA PARA NUEVA RESEÑA
  seleccionarEstrella(n:number){ this.miRating = n; }

  // ENVÍO DE RESEÑA: VALIDA, CREA Y REFRESCA ESTADO
  enviarResena(){
    if (!this.productoSeleccionado || this.miRating<1 || !this.miComentario.trim()) return; // VALIDACIÓN BÁSICA

    this.reviewsSvc.crear(this.productoSeleccionado.id, this.miRating, this.miComentario.trim())
      .subscribe({
        next: _ => {
          // RECARGA RESEÑAS Y SINCRONIZA PROMEDIO/TOTALES EN LISTAS
          this.cargarResenas(this.productoSeleccionado!.id, true);

          // LIMPIA FORMULARIO
          this.miRating = 0; this.miComentario = '';

          // RECARGA POR SI CAMBIÓ EL LISTADO (REDUNDANCIA ACEPTABLE)
          this.cargarResenas(this.productoSeleccionado!.id);

          // ACTUALIZA CONTADOR LOCAL DE RESEÑAS DEL PRODUCTO SELECCIONADO
          this.productoSeleccionado!.totalResenas = (this.productoSeleccionado!.totalResenas||0)+1;
        },
        error: e => alert(e?.error?.message || 'No se pudo enviar la reseña') // FEEDBACK DE ERROR
      });
  }

  // SINCRONIZA PROMEDIO Y TOTAL DE RESEÑAS EN LAS LISTAS VISUALES
  private syncProductoCalificacion(id: number, prom: number, total: number) {
    const upd = (x: any) => x.id === id ? { ...x, ratingPromedio: prom, totalResenas: total } : x;
    this.productos = this.productos.map(upd);
    this.filtrados = this.filtrados.map(upd);
  }

  // FILTRA POR NOMBRE Y CATEGORÍA 
  aplicarFiltro() {
    const nombre = this.filtroNombre.toLowerCase();
    const categoria = this.filtroCategoria;
    this.filtrados = this.productos.filter(p => {
      const matchNombre = p.nombre.toLowerCase().includes(nombre);
      const matchCategoria = categoria === 'Todos' || p.categoria === categoria;
      return matchNombre && matchCategoria;
    });
  }

  // AGREGA AL CARRITO Y NAVEGA A LA VISTA DEL CARRITO
  addToCart(id: number) {
    this.carrito.agregar(id, 1).subscribe({
      next: _ => this.router.navigateByUrl('/carrito'),
      error: _ => alert('No se pudo agregar al carrito') 
    });
  }

  
  asQ(q: number) { return q.toLocaleString('es-GT', { style: 'currency', currency: 'GTQ' }); }
}
