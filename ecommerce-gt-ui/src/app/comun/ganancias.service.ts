import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

// DTO PRINCIPAL PARA RESUMEN DE GANANCIAS
export interface GananciaResumenDTO {
  totalNeto: number;   
  netoHoy: number;     
  netoMes: number;    
  ventasHoy: number;   
  ventasMes: number;   
}

// DTO DETALLADO DE CADA VENTA / PEDIDO
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

// MODELO DE PAGINACIÓN GENÉRICO
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

  // OBTIENE RESUMEN GLOBAL DE GANANCIAS (TOTALES Y PROMEDIOS)
  resumen(){
    return this.http.get<GananciaResumenDTO>(`${this.base}/resumen`);
  }

  // LISTA DETALLE DE GANANCIAS PAGINADAS POR PEDIDO O PRODUCTO
  listar(pagina=0, tamanio=12){
    return this.http.get<Page<GananciaItemDTO>>(this.base, { params: { pagina, tamanio }});
  }
}
