import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface CarritoItemDTO {
  productoId: number;
  nombre: string;
  imagenUrl: string | null;
  precio: number;
  cantidad: number;
  subtotal: number;
  disponible: number; 
}
export interface CarritoDTO {
  id: number;
  items: CarritoItemDTO[];
  total: number;
}

export interface TarjetaGuardadaLite {
  id: number;
  ultimos4: string;
  marca: string;
  expiracionMes: number;
  expiracionAnio: number;
  titular: string;
}


@Injectable({ providedIn: 'root' })
export class CarritoService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/carrito`;

    tarjetas() {
    return this.http.get<TarjetaGuardadaLite[]>(`${this.base}/tarjetas`, { headers: this.authHeaders() });
  }
    simularTokenPasarela(): string {
    return 'SIM-' + Date.now() + '-' + Math.random().toString(36).slice(2, 8).toUpperCase();
  }

private authHeaders(): HttpHeaders {
  const token = localStorage.getItem('token');
  return new HttpHeaders({
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json'
  });
}



  ver() {
    return this.http.get<CarritoDTO>(this.base, { headers: this.authHeaders() });
  }

  agregar(productoId: number, cantidad = 1) {
    return this.http.post<CarritoDTO>(`${this.base}/agregar`,
      { productoId, cantidad }, { headers: this.authHeaders() });
  }

  actualizar(productoId: number, cantidad: number) {
    return this.http.put<CarritoDTO>(`${this.base}/item`,
      { productoId, cantidad }, { headers: this.authHeaders() });
  }

  eliminar(productoId: number) {
    return this.http.delete<CarritoDTO>(`${this.base}/item/${productoId}`,
      { headers: this.authHeaders() });
  }

  vaciar() {
    return this.http.delete<void>(this.base, { headers: this.authHeaders() });
  }

  checkout(payload: {
    tarjetaGuardadaId?: number;
    tokenPasarela?: string;
    ultimos4?: string;
    marca?: string;
    expiracionMes?: number;
    expiracionAnio?: number;
    titular?: string;
    guardarTarjeta?: boolean;
  }) {
    return this.http.post<{ pedidoId: number }>(
      `${this.base}/checkout`,
      payload,
      { headers: this.authHeaders() }
    );
  }
}
