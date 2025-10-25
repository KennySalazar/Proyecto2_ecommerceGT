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

  listarPendientes(pagina=0, tamanio=12){
    return this.http.get<SpringPage<ModProducto>>(`${this.base}/solicitudes`, {params:{pagina, tamanio}});
  }
  aprobar(id:number){
    return this.http.post(`${this.base}/solicitudes/${id}/aprobar`, {});
  }
  rechazar(id:number, motivo:string){
    return this.http.post(`${this.base}/solicitudes/${id}/rechazar`, { motivo });
  }

listarTodos(pagina=0, tamanio=12){
  return this.http.get<SpringPage<ModProducto>>(
    `${this.base}/historial`, { params: { pagina, tamanio } }
  );
}

listarHistorial(pagina=0, tamanio=12){
  return this.http.get<SpringPage<ModProducto>>(`${this.base}/historial`, {
    params: { pagina, tamanio }
  });
}
}