import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface LoginRequest { correo: string; password: string; }
export interface LoginResponse { token: string; nombre?: string; rol?: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  login(body: LoginRequest) {
    // en dev, gracias al proxy, /api va al 8080
    return this.http.post<LoginResponse>(`${environment.apiBase}/auth/login`, body);
  }

  register(body: { nombre: string; correo: string; telefono: string; password: string; }) {
  return this.http.post<any>(`${environment.apiBase}/auth/register`, body);
}
}
