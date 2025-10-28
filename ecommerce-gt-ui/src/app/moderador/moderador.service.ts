import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface ModProducto {
  id:number; nombre:string; categoria:string;
  precioCents:number; stock:number;
  vendedorNombre:string; imagenUrl:string|null; creadoEnIso:string;
  estadoMod: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO';
  comentarioRechazo?: string | null;
}

export interface SpringPage<T> {
  content:T[]; totalPages:number; totalElements:number; number:number; size:number;
}

@Injectable({providedIn:'root'})
export class ModeradorService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/moderador`;

  // LISTA PRODUCTOS PENDIENTES DE REVISIÓN
  listarPendientes(pagina=0, tamanio=12){
    return this.http.get<SpringPage<ModProducto>>(`${this.base}/solicitudes`, {params:{pagina, tamanio}});
  }

  // APRUEBA UN PRODUCTO EN REVISIÓN
  aprobar(id:number){
    return this.http.post(`${this.base}/solicitudes/${id}/aprobar`, {});
  }

  // RECHAZA UN PRODUCTO Y ENVÍA EL MOTIVO
  rechazar(id:number, motivo:string){
    return this.http.post(`${this.base}/solicitudes/${id}/rechazar`, { motivo });
  }

  // LISTA TODO EL HISTORIAL DE PRODUCTOS MODERADOS (DUPLICADO)
  listarTodos(pagina=0, tamanio=12){
    return this.http.get<SpringPage<ModProducto>>(
      `${this.base}/historial`, { params: { pagina, tamanio } }
    );
  }

  // LISTA EL HISTORIAL DE PRODUCTOS (VERSIÓN PRINCIPAL)
  listarHistorial(pagina=0, tamanio=12){
    return this.http.get<SpringPage<ModProducto>>(`${this.base}/historial`, {
      params: { pagina, tamanio }
    });
  }
}
