import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

// SERVICIO PARA OBTENER LAS GANANCIAS DEL ADMINISTRADOR
@Injectable({ providedIn: 'root' })
export class GananciasAdminService {
  
  private http = inject(HttpClient);

  // URL BASE DEL ENDPOINT DE GANANCIAS
  private base = `${environment.apiBase}/admin/ganancias`;

  // OBTIENE EL TOTAL DE GANANCIAS ENTRE DOS FECHAS
  obtener(desde: string, hasta: string) {
    // REALIZA LA PETICIÓN GET CON LOS PARÁMETROS DESDE Y HASTA
    return this.http.get<{ total: number }>(this.base, { params: { desde, hasta } });
  }
}
