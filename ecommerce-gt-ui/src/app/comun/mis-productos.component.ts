import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductosService, ProductoCard, SpringPage } from './productos.service';
import { environment } from '../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-mis-productos',
  imports: [CommonModule, RouterLink],
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

  ngOnInit(){ this.cargar(); }

  fullImg(p: any): string | null {
  const u: string | null = p?.imagenUrl ?? p?.imageUrl ?? null; 
  return u ? this.backendOrigin + u : null;
}

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
