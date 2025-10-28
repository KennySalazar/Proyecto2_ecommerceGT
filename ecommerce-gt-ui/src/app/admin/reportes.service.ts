import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

// INTERFAZ PARA LOS PRODUCTOS MÁS VENDIDOS
export interface ProductoTopDTO { 
  productoId: number; 
  nombre: string; 
  unidadesVendidas: number; 
  totalVendido: number; 
}

// INTERFAZ PARA CLIENTES O VENDEDORES CON CANTIDAD DE VENTAS O PEDIDOS
export interface TopClienteCantidadDTO { 
  usuarioId: number; 
  nombre: string; 
  cantidad: number; 
}

// INTERFAZ PARA LOS VENDEDORES CON MEJOR GANANCIA
export interface topVendedoresDTO { 
  usuarioId: number; 
  nombre: string; 
  totalNeto: number; 
  totalVentas: number; 
}

// INTERFAZ PARA PAGINACIÓN DE RESULTADOS
export interface Page<T> { 
  content: T[]; 
  totalElements: number; 
  totalPages: number; 
  number: number; 
  size: number; 
}

// INTERFAZ PARA LAS NOTIFICACIONES REGISTRADAS
export interface NotificacionRowDTO {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  tipo: string;
  asunto: string;
  enviado: boolean;
  enviadoEn?: string | null;
  creadoEn: string;
}

// SERVICIO QUE OBTIENE LOS REPORTES DE VENTAS, CLIENTES, INVENTARIO Y NOTIFICACIONES
@Injectable({ providedIn: 'root' })
export class ReportesService {
  
  private http = inject(HttpClient);

  // URL DE LOS ENDPOINTS DE REPORTES
  private base = `${environment.apiBase}/admin/reportes`;

  // OBTIENE EL TOP DE PRODUCTOS MÁS VENDIDOS
  topProductos(desde: string, hasta: string, limit = 10) {
    return this.http.get<ProductoTopDTO[]>(`${this.base}/top-productos`, { params: { desde, hasta, limit } });
  }

  // OBTIENE LOS CLIENTES CON MAYOR GANANCIA
  topClientesGanancia(desde: string, hasta: string, limit = 5) {
    return this.http.get<topVendedoresDTO[]>(`${this.base}/top-clientes-ganancia`, { params: { desde, hasta, limit } });
  }

  // OBTIENE LOS VENDEDORES CON MEJORES RESULTADOS
  topVendedores(desde: string, hasta: string, limit = 5) {
    return this.http.get<TopClienteCantidadDTO[]>(`${this.base}/top-vendedores`, { params: { desde, hasta, limit } });
  }

  // OBTIENE LOS CLIENTES CON MÁS PEDIDOS REALIZADOS
  topClientesPedidos(desde: string, hasta: string, limit = 10) {
    return this.http.get<TopClienteCantidadDTO[]>(`${this.base}/top-clientes-pedidos`, { params: { desde, hasta, limit } });
  }

  // OBTIENE EL TOP DE PRODUCTOS SEGÚN EL INVENTARIO
  topInventario(limit = 10) {
    return this.http.get<TopClienteCantidadDTO[]>(`${this.base}/top-inventario`, { params: { limit } as any });
  }

  // OBTIENE LAS NOTIFICACIONES EN UN RANGO DE FECHAS
  notificaciones(desde: string, hasta: string) {
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<NotificacionRowDTO[]>(`${this.base}/notificaciones`, { params });
  }
}
