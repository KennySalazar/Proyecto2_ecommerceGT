import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface PedidoItemLiteDTO {
  productoId: number;
  nombre: string;
  imagenUrl: string | null;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface PedidoResumenDTO {
  id: number;
  fecha: string; 
  total: number;
  estado: string;
  fechaEstimadaEntrega?: string | null;
  items: PedidoItemLiteDTO[];
}

@Injectable({ providedIn: 'root' })
export class PedidosService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/pedidos`;

  // CONSTRUYE ENCABEZADOS CON TOKEN JWT PARA AUTENTICACIÓN
  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // OBTIENE TODOS LOS PEDIDOS DEL USUARIO ACTUAL
  misPedidos() {
    return this.http.get<PedidoResumenDTO[]>(`${this.base}/mios`, { headers: this.authHeaders() });
  }
}
