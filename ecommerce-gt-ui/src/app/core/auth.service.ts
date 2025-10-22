import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  correo: string; 
  password: string;
}

export interface LoginResponse {
  token: string;
  nombre?: string;
  rolCodigo?: 'ADMIN' | 'MODERADOR' | 'LOGISTICA' | 'COMUN';
  rol?: 'ADMIN' | 'MODERADOR' | 'LOGISTICA' | 'COMUN';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  
  private _token = signal<string | null>(localStorage.getItem('token'));
  isLoggedIn(): boolean {
  return !!localStorage.getItem('token');
}

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

    
    this._token.set(resp.token ?? '');
  }

  obtenerRol(): string | null {
    return localStorage.getItem('rol');
  }

 
  get token(): string | null {
    return this._token();
  }


 
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('nombre');

    this._token.set(null);
    this.router.navigateByUrl('/login');
  }

  
}
