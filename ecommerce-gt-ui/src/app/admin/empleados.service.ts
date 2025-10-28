import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

// INTERFAZ QUE REPRESENTA UNA FILA DE EMPLEADO EN LAS LISTAS
export interface EmpleadoFila {
  id: number; 
  nombre: string; 
  correo: string; 
  telefono: string;
  rol: 'ADMIN' | 'MODERADOR' | 'LOGISTICA' | 'COMUN'; 
  activo: boolean;
}

// INTERFAZ PARA LA ESTRUCTURA DE PAGINACIÓN DEVUELTA POR SPRING
export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; 
  size: number;
}

// SERVICIO QUE MANEJA LAS PETICIONES HTTP RELACIONADAS CON EMPLEADOS
@Injectable({ providedIn: 'root' })
export class EmpleadosService {
  private http = inject(HttpClient);

  // URL BASE PARA LAS PETICIONES AL BACKEND
  private base = `${environment.apiBase}/admin/empleados`;

  // OBTIENE UNA LISTA DE EMPLEADOS CON FILTROS Y PAGINACIÓN
  listar(pagina: number, tamanio: number, filtros?: { nombre?: string; rol?: string }) {
    let params = new HttpParams()
      .set('pagina', pagina)
      .set('tamanio', tamanio);

    // AGREGA FILTRO POR NOMBRE SI EXISTE
    if (filtros?.nombre && filtros.nombre.trim()) {
      params = params.set('nombre', filtros.nombre.trim());
    }

    // AGREGA FILTRO POR ROL SI NO ES "TODOS"
    if (filtros?.rol && filtros.rol !== 'TODOS') {
      params = params.set('rol', filtros.rol);
    }

    // REALIZA LA PETICIÓN GET CON LOS PARÁMETROS
    return this.http.get<SpringPage<EmpleadoFila>>(`${environment.apiBase}/admin/empleados`, { params });
  }

  // CREA UN NUEVO EMPLEADO
  crear(body: {
    nombre: string;
    correo: string;
    telefono?: string;
    contrasena: string;
    rolCodigo: 'ADMIN' | 'MODERADOR' | 'LOGISTICA';
  }) {
    return this.http.post<EmpleadoFila>(this.base, body);
  }

  // OBTIENE UN EMPLEADO POR SU ID
  get(id: number) {
    return this.http.get<EmpleadoFila>(`${this.base}/${id}`);
  }

  // ACTUALIZA UN EMPLEADO EXISTENTE
  actualizar(id: number, body: {
    nombre: string;
    correo: string;
    telefono?: string;
    contrasena?: string; 
    rolCodigo?: 'ADMIN' | 'MODERADOR' | 'LOGISTICA';
  }) {
    return this.http.put<EmpleadoFila>(`${this.base}/${id}`, body);
  }
}
