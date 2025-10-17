// src/app/admin/empleados.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface EmpleadoFila {
  id: number;
  nombre: string;
  correo: string;
  telefono: string;
  rol: string;
  activo: boolean;
}

// *** Estructura que devuelve Spring Data Page ***
export interface SpringPage<T> {
  content: T[];
  number: number;
  totalPages: number;
  totalElements: number;
}

@Injectable({ providedIn: 'root' })
export class EmpleadosService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/admin/empleados`;

  listar(pagina = 0, tam = 10) {
    const params = new HttpParams().set('pagina', pagina).set('tamanio', tam);
    return this.http.get<SpringPage<EmpleadoFila>>(this.base, { params });
  }

  listarComunes(pagina = 0, tam = 10) {
    const params = new HttpParams().set('pagina', pagina).set('tamanio', tam);
    return this.http.get<SpringPage<EmpleadoFila>>(`${this.base}/comunes`, { params });
  }

  // IMPORTANTE: tu backend espera 'contrasena'
  crear(body: {
    nombre: string;
    correo: string;
    telefono?: string;
    contrasena: string;
    rolCodigo: 'ADMIN' | 'MODERADOR' | 'LOGISTICA';
  }) {
    return this.http.post<EmpleadoFila>(this.base, body);
  }
}
