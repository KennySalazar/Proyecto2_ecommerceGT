import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductosService, ProductoResponse, SpringPage } from './productos.service';
import { environment } from '../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-catalogo',
  imports: [CommonModule],
  templateUrl: './catalogo.component.html'
})
export class CatalogoComponent {
  private svc = inject(ProductosService);
  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');
  productos: ProductoResponse[] = [];
  pagina = 0; tamanio = 12; totalPaginas = 0; total = 0;

    fullImg(p: any): string | null {
  const u: string | null = p?.imagenUrl ?? p?.imageUrl ?? null; 
  return u ? this.backendOrigin + u : null;
}



  ngOnInit() { this.cargar(); }

  cargar() {
    this.svc.listarPublico(this.pagina, this.tamanio).subscribe((page: SpringPage<ProductoResponse>) => {
      this.productos = page.content;
      this.totalPaginas = page.totalPages;
      this.pagina = page.number;
      this.total = page.totalElements;
    });
  }
  next(){ if(this.pagina+1<this.totalPaginas){ this.pagina++; this.cargar(); } }
  prev(){ if(this.pagina>0){ this.pagina--; this.cargar(); } }

  asQ(quetzales:number){ return (quetzales).toLocaleString('es-GT',{ style:'currency', currency:'GTQ'}); }
}
