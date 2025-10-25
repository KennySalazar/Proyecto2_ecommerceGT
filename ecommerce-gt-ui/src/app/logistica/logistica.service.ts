import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface PedidoLogisticaDTO {
  id:number;
  compradorNombre:string;
  fechaCreacion:string;
  fechaEstimadaEntrega:string;
  totalBruto:number;
  estadoCodigo:'EN_CURSO'|'ENTREGADO'|string;
}

export interface SpringPage<T> {
  content:T[]; totalPages:number; number:number; size:number; totalElements:number;
}

@Injectable({ providedIn: 'root' })
export class LogisticaService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/logistica`;

  enCurso(pagina=0, tamanio=12) {
    return this.http.get<SpringPage<PedidoLogisticaDTO>>(
      `${this.base}/en-curso`, { params:{ pagina, tamanio } }
    );
  }

  actualizarFechaEntrega(id:number, fechaISO:string) {
    return this.http.put<void>(`${this.base}/${id}/fecha-entrega`, null, { params:{ fecha: fechaISO } });
  }

  marcarEntregado(id:number) {
    return this.http.post<void>(`${this.base}/${id}/entregar`, {});
  }
}