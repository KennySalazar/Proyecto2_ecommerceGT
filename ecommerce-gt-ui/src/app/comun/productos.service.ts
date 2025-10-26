import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface ProductoResponse {
  id: number;
  nombre: string;
  descripcion: string;
  precioCents: number;
  stock: number;
  estadoArticulo: 'NUEVO'|'USADO';
  categoria: string;
  estadoModeracion: 'PENDIENTE'|'APROBADO'|'RECHAZADO';
    imagenUrl: string | null; 
  vendedorId: number;
  ratingPromedio?: number;
  totalResenas?: number;
  categoriaId?: number;
}

export interface SpringPage<T> {
  numberOfElements: number;
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ResenaDTO {
  usuario: string;         
  rating: number;          
  comentario: string;
  fecha: string;           
}

export interface ResenasResponse {
  promedio: number;        
  total: number;        
  items: ResenaDTO[];
}
export type ProductoCard = ProductoResponse;



@Injectable({ providedIn: 'root' })
export class ProductosService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/productos`;

  listarPublico(pagina=0, tamanio=12) {
    return this.http.get<SpringPage<ProductoResponse>>(`${this.base}/publico`, {
      params: { pagina, tamanio }
    });
  }

  listarMisProductos(pagina=0, tamanio=10) {
    return this.http.get<SpringPage<ProductoResponse>>(`${this.base}/mis`, {
      params: { pagina, tamanio }
    });
  }

  crear(dto: any, imagen?: File) {
    const fd = new FormData();
    fd.append('dto', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
    if (imagen) fd.append('imagen', imagen);
    return this.http.post<ProductoResponse>(`${this.base}/mis`, fd);
  }

  actualizar(id: number, dto: any, imagen?: File) {
    const fd = new FormData();
    fd.append('dto', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
    if (imagen) fd.append('imagen', imagen);
    return this.http.put<ProductoResponse>(`${this.base}/mis/${id}`, fd);
  }

    mis(pagina = 0, tamanio = 12) {
    return this.listarMisProductos(pagina, tamanio);
    }

    obtenerMio(id: number) {
      return this.http.get<ProductoResponse>(`${this.base}/mis/${id}`);
    }

    
  obtenerResenas(productoId: number) {
    return this.http.get<ResenasResponse>(`${this.base}/${productoId}/resenas`);
  }

  
  crearResena(productoId: number, payload: { rating: number; comentario: string }) {
    return this.http.post(`${this.base}/${productoId}/resenas`, payload);
  }

  
}