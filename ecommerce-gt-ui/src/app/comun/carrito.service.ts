import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

// DEFINIMOS INTERFAZ PARA CADA ITEM DEL CARRITO
export interface CarritoItemDTO {
  productoId: number; 
  nombre: string; 
  imagenUrl: string | null; 
  precio: number; 
  cantidad: number; 
  subtotal: number; 
  disponible: number; 
}

// DEFINIMOS INTERFAZ DEL CARRITO 
export interface CarritoDTO {
  id: number; 
  items: CarritoItemDTO[]; 
  total: number; 
}

// DEFINIMOS INTERFAZ PARA TARJETAS GUARDADAS LIGERAS
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

  // BASE URL DEL CARRITO SEGÚN EL ENVIRONMENT
  private base = `${environment.apiBase}/carrito`;

  // MÉTODO PARA OBTENER LAS TARJETAS GUARDADAS
  tarjetas() {
    return this.http.get<TarjetaGuardadaLite[]>(`${this.base}/tarjetas`, { headers: this.authHeaders() });
  }

  // SIMULA UN TOKEN DE PAGO PARA LA PASARELA
  simularTokenPasarela(): string {
    return 'SIM-' + Date.now() + '-' + Math.random().toString(36).slice(2, 8).toUpperCase();
  }

  // MÉTODO PRIVADO PARA OBTENER LOS HEADERS CON AUTENTICACIÓN
  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      Authorization: `Bearer ${token}`, 
      'Content-Type': 'application/json' 
    });
  }

  // OBTENER EL CARRITO COMPLETO
  ver() {
    return this.http.get<CarritoDTO>(this.base, { headers: this.authHeaders() });
  }

  // AGREGAR UN PRODUCTO AL CARRITO
  agregar(productoId: number, cantidad = 1) {
    return this.http.post<CarritoDTO>(`${this.base}/agregar`,
      { productoId, cantidad }, { headers: this.authHeaders() });
  }

  // ACTUALIZAR LA CANTIDAD DE UN PRODUCTO EN EL CARRITO
  actualizar(productoId: number, cantidad: number) {
    return this.http.put<CarritoDTO>(`${this.base}/item`,
      { productoId, cantidad }, { headers: this.authHeaders() });
  }

  // ELIMINAR UN PRODUCTO DEL CARRITO
  eliminar(productoId: number) {
    return this.http.delete<CarritoDTO>(`${this.base}/item/${productoId}`,
      { headers: this.authHeaders() });
  }

  // VACIAR TODO EL CARRITO
  vaciar() {
    return this.http.delete<void>(this.base, { headers: this.authHeaders() });
  }

  // REALIZAR EL CHECKOUT DEL CARRITO
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
