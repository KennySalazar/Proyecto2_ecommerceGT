import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface ProductoTopDTO { productoId:number; nombre:string; unidadesVendidas:number; totalVendido:number; }
export interface TopClienteCantidadDTO { usuarioId:number; nombre:string; cantidad:number; }
export interface topVendedoresDTO { usuarioId:number; nombre:string; totalNeto:number; totalVentas:number; }

export interface Page<T> { content:T[]; totalElements:number; totalPages:number; number:number; size:number; }
export interface NotificacionRowDTO {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  tipo: string;
  asunto: string;
  enviado: boolean;
  enviadoEn?: string|null;
  creadoEn: string;
}
@Injectable({ providedIn: 'root' })
export class ReportesService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/admin/reportes`;

  topProductos(desde:string, hasta:string, limit=10){
    return this.http.get<ProductoTopDTO[]>(`${this.base}/top-productos`, { params:{ desde, hasta, limit }});
  }
  topClientesGanancia(desde:string, hasta:string, limit=5){
    return this.http.get<topVendedoresDTO[]>(`${this.base}/top-clientes-ganancia`, { params:{ desde, hasta, limit }});
  }
  topVendedores(desde:string, hasta:string, limit=5){
    return this.http.get<TopClienteCantidadDTO[]>(`${this.base}/top-vendedores`, { params:{ desde, hasta, limit }});
  }
  topClientesPedidos(desde:string, hasta:string, limit=10){
    return this.http.get<TopClienteCantidadDTO[]>(`${this.base}/top-clientes-pedidos`, { params:{ desde, hasta, limit }});
  }
  topInventario(limit=10){
    return this.http.get<TopClienteCantidadDTO[]>(`${this.base}/top-inventario`, { params:{ limit } as any });
  }
  notificaciones(desde: string, hasta: string){
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<NotificacionRowDTO[]>(`${this.base}/notificaciones`, { params });
  }
}
