import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface EmpleadoFila {
  id: number; nombre: string; correo: string; telefono: string;
  rol: 'ADMIN'|'MODERADOR'|'LOGISTICA'|'COMUN'; activo: boolean;
}

// *** Estructura que devuelve Spring Data Page ***
export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; 
  size: number;

}

@Injectable({ providedIn: 'root' })
export class EmpleadosService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/admin/empleados`;

  listar(pagina: number, tamanio: number, filtros?: { nombre?: string; rol?: string }) {
    let params = new HttpParams()
      .set('pagina', pagina)
      .set('tamanio', tamanio);

    if (filtros?.nombre && filtros.nombre.trim()) {
      params = params.set('nombre', filtros.nombre.trim());
    }
    if (filtros?.rol && filtros.rol !== 'TODOS') {
      params = params.set('rol', filtros.rol);
    }

    return this.http.get<SpringPage<EmpleadoFila>>(`${environment.apiBase}/admin/empleados`, { params });
  }


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
