// src/app/core/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  correo: string; password: string;
}

// Ajusta el nombre del campo del rol según tu backend: 'rolCodigo' o 'rol'
export interface LoginResponse {
  token: string;
  nombre?: string;
  rolCodigo?: 'ADMIN'|'MODERADOR'|'LOGISTICA'|'COMUN';
  rol?: 'ADMIN'|'MODERADOR'|'LOGISTICA'|'COMUN';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  login(body: LoginRequest) {
    return this.http.post<LoginResponse>(`${environment.apiBase}/auth/login`, body);
  }

  register(body: { nombre: string; correo: string; telefono: string; password: string; }) {
    return this.http.post<any>(`${environment.apiBase}/auth/register`, body);
  }

  guardarSesion(resp: LoginResponse) {
    localStorage.setItem('token', resp.token ?? '');
    const rol = (resp.rolCodigo ?? resp.rol ?? 'COMUN');
    localStorage.setItem('rol', rol);
    if (resp.nombre) localStorage.setItem('nombre', resp.nombre);
  }

  obtenerRol(): string | null {
    return localStorage.getItem('rol');
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('nombre');
  }
}
