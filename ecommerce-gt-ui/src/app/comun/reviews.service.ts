import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewsService {
  private http = inject(HttpClient);
  base = `${environment.apiBase}/productos`;

  // OBTIENE LAS RESEÑAS DE UN PRODUCTO 
  listar(productoId: number) {
    return this.http.get<{promedio:number,total:number,puedeComentar:boolean,items:any[]}>
      (`${this.base}/${productoId}/reviews`);
  }

  // CREA UNA NUEVA RESEÑA PARA EL PRODUCTO
  crear(productoId: number, rating: number, comentario: string) {
    return this.http.post(`${this.base}/${productoId}/reviews`, { rating, comentario });
  }
}
