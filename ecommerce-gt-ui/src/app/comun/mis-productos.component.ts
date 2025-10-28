import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductosService, ProductoCard, SpringPage } from './productos.service';
import { environment } from '../../environments/environment';
import { ImgUrlPipe } from '../shared/pipes/img-url.pipe';

@Component({
  standalone: true,
  selector: 'app-mis-productos',
  imports: [CommonModule, RouterLink, ImgUrlPipe],
  templateUrl: './mis-productos.component.html'
})
export class MisProductosComponent implements OnInit {
  private api = inject(ProductosService);

  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');
  productos: ProductoCard[] = [];
  pagina = 0;
  tamanio = 12;
  totalPaginas = 0;
  total = 0;

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit(){ this.cargar(); }

  // OBTIENE URL COMPLETA DE IMAGEN O DEVUELVE IMAGEN POR DEFECTO
  fullImg(p: any): string {
    const u: string | null = p?.imagenUrl ?? p?.imageUrl ?? null;
    if (!u) return 'assets/noimg.png';
    return u.startsWith('/uploads/') ? u : '/uploads/' + u.replace(/^\/+/, '');
  }

  // CARGA LOS PRODUCTOS DEL USUARIO LOGUEADO
  cargar() {
    this.api.mis(this.pagina, this.tamanio).subscribe((p: SpringPage<ProductoCard>) => {
      this.productos = p.content;
      this.total = p.totalElements;
      this.totalPaginas = p.totalPages;
      this.pagina = p.number;
    });
  }


  next(){ if (this.pagina + 1 < this.totalPaginas){ this.pagina++; this.cargar(); } }

  prev(){ if (this.pagina > 0){ this.pagina--; this.cargar(); } }

  toQ(quetzales:number){ return (quetzales).toLocaleString('es-GT',{style:'currency',currency:'GTQ'}) }
}
