import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface GananciaResumenDTO {
  totalNeto: number;
  netoHoy: number;
  netoMes: number;
  ventasHoy: number;
  ventasMes: number;
}

export interface GananciaItemDTO {
  pedidoId: number;
  fecha: string;         
  productoId: number;
  productoNombre: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  comision: number;
  netoVendedor: number;
  estadoPedido: string;
  imagenUrl?: string | null;
}

export interface Page<T>{
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class GananciasService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/ganancias`;

  resumen(){
    return this.http.get<GananciaResumenDTO>(`${this.base}/resumen`);
  }
  listar(pagina=0, tamanio=12){
    return this.http.get<Page<GananciaItemDTO>>(this.base, { params: { pagina, tamanio }});
  }
}