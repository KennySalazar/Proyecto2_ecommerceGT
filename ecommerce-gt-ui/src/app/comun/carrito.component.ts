import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CarritoDTO, CarritoItemDTO, CarritoService, TarjetaGuardadaLite } from './carrito.service';
import { environment } from '../../environments/environment';
import { FormsModule } from '@angular/forms';
import { ImgUrlPipe } from '../shared/pipes/img-url.pipe';

// COMPONENTE PARA MANEJAR EL CARRITO DE COMPRAS
@Component({
  standalone: true,
  selector: 'app-carrito',
  imports: [CommonModule, FormsModule, ImgUrlPipe],
  templateUrl: './carrito.component.html'
})
export class CarritoComponent implements OnInit {
  private svc = inject(CarritoService);

  // DATOS DEL CARRITO Y ESTADO DE CARGA
  data?: CarritoDTO;
  cargando = false;

  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');

  // ALERTAS PARA EL USUARIO
  alerta = {
    show: false,
    titulo: '',
    mensaje: '',
    tipo: 'success' as 'success' | 'error' | 'info',
    onClose: undefined as (() => void) | undefined,
    timeoutId: undefined as any
  };

  // MUESTRA UNA ALERTA
  mostrarAlerta(titulo: string, mensaje: string, tipo: 'success' | 'error' | 'info' = 'info', autoClose = false, duracion = 0, onClose?: () => void) {
    if (this.alerta.timeoutId) clearTimeout(this.alerta.timeoutId);
    this.alerta = { show: true, titulo, mensaje, tipo, onClose, timeoutId: undefined };
    if (autoClose && duracion > 0) this.alerta.timeoutId = setTimeout(() => this.cerrarAlerta(), duracion);
  }

  // CIERRA LA ALERTA
  cerrarAlerta() {
    if (this.alerta.timeoutId) clearTimeout(this.alerta.timeoutId);
    const onCloseFn = this.alerta.onClose;
    this.alerta.show = false;
    if (onCloseFn) onCloseFn();
  }

  // CONFIRMACIONES 
  confirmOpen = false;
  confirmText = '';
  private confirmResolver?: (ok: boolean) => void;

  askConfirm(message: string): Promise<boolean> {
    this.confirmText = message;
    this.confirmOpen = true;
    return new Promise<boolean>(resolve => this.confirmResolver = resolve);
  }
  confirmYes(){ this.confirmOpen = false; this.confirmResolver?.(true); }
  confirmNo(){  this.confirmOpen = false; this.confirmResolver?.(false); }

  // CARGA INICIAL DEL CARRITO
  ngOnInit(){ this.cargar(); }

  // OBTIENE LA URL DE LA IMAGEN DEL PRODUCTO
  imgSrc(it: CarritoItemDTO): string {
    const u = it?.imagenUrl ?? null;
    if (!u) return 'assets/noimg.png';
    return u.startsWith('/uploads/') ? u : '/uploads/' + u.replace(/^\/+/, '');
  }

  // CARGA EL CARRITO DESDE EL SERVICIO
  cargar(){
    this.cargando = true;
    this.svc.ver().subscribe({
      next: d => { this.data = d; this.cargando=false; },
      error: _ => { this.cargando=false; }
    });
  }

  // AUMENTA LA CANTIDAD DE UN ITEM
  inc(it: CarritoItemDTO){
    if (it.disponible === 0) { this.mostrarAlerta('Ya no hay más unidades disponibles', 'info'); return; }
    this.svc.actualizar(it.productoId, it.cantidad + 1).subscribe({
      next: d => this.data = d,
      error: e => this.mostrarAlerta(e?.error?.message || 'No se pudo aumentar la cantidad', 'error')
    });
  }

  // DISMINUYE LA CANTIDAD DE UN ITEM
  dec(it: CarritoItemDTO){
    const n = Math.max(1, it.cantidad - 1);
    this.svc.actualizar(it.productoId, n).subscribe({
      next: d => this.data = d,
      error: e => this.mostrarAlerta(e?.error?.message || 'No se pudo disminuir la cantidad', 'error')
    });
  }

  // ELIMINA UN ITEM DEL CARRITO
  remove(it: CarritoItemDTO){
    this.svc.eliminar(it.productoId).subscribe({
      next: d => { this.data = d; this.mostrarAlerta('Producto eliminado del carrito', 'info'); },
      error: e => this.mostrarAlerta(e?.error?.message || 'No se pudo eliminar el producto', 'error')
    });
  }

  // VACIAR CARRITO CON CONFIRMACIÓN
  async vaciar(){
    if (!this.data || this.data.items.length === 0) return;
    const ok = await this.askConfirm('¿Vaciar carrito?');
    if (!ok) return;
    this.svc.vaciar().subscribe({
      next: _ => { this.cargar(); this.mostrarAlerta('Carrito vaciado', 'info'); },
      error: e => this.mostrarAlerta(e?.error?.message || 'No se pudo vaciar el carrito', 'error')
    });
  }

  // AGREGAR PRODUCTO DESDE CATÁLOGO
  agregarDesdeCatalogo(productoId: number){
    this.svc.agregar(productoId, 1).subscribe({
      next: _ => { this.cargar(); this.mostrarAlerta('Agregado al carrito', 'success'); },
      error: e => this.mostrarAlerta(e?.error?.message || 'No se pudo agregar: sin stock', 'error')
    });
  }

  // FORMATO MONEDA GTQ
  asQ(q: number){ return q.toLocaleString('es-GT', { style:'currency', currency:'GTQ' }); }

  // PAGOS
  showPago = false;
  tarjetas: TarjetaGuardadaLite[] = [];
  modo: 'guardada' | 'nueva' = 'guardada';
  tarjetaGuardadaId?: number;

  formNueva = { numero: '', marca: 'VISA', mes: '', anio: '', titular: '', guardar: true };

  // ABRIR MODAL DE PAGO
  abrirPago() {
    if (!this.data || this.data.items.length === 0) return;
    this.svc.tarjetas().subscribe(ts => {
      this.tarjetas = ts;
      this.modo = this.tarjetas.length ? 'guardada' : 'nueva';
      this.tarjetaGuardadaId = this.tarjetas.length ? this.tarjetas[0].id : undefined;
      this.showPago = true;
    });
  }

  cerrarPago(){ this.showPago = false; }

  // VALIDACIÓN DE TARJETA NUEVA
  get nuevaValida(): boolean {
    const n = (this.formNueva.numero || '').replace(/\s+/g,'');
    const mes = Number(this.formNueva.mes);
    const anio = Number(this.formNueva.anio);
    const titularOk = (this.formNueva.titular || '').trim().length >= 4;
    const numOk = /^\d{13,19}$/.test(n);
    const mesOk = mes >= 1 && mes <= 12;
    const anioOk = anio >= 2024 && anio <= 2100;
    return numOk && mesOk && anioOk && titularOk;
  }

  // REALIZA EL PAGO
  pagar() {
    if (!this.data || this.data.items.length === 0) return;

    // USANDO TARJETA GUARDADA
    if (this.modo === 'guardada') {
      if (!this.tarjetaGuardadaId) {
        this.mostrarAlerta('Atención', 'Seleccione una tarjeta para continuar.', 'info', false);
        return;
      }

      this.svc.checkout({ tarjetaGuardadaId: this.tarjetaGuardadaId }).subscribe({
        next: r => {
          this.showPago = false;
          this.mostrarAlerta('Pago aprobado', `Pedido #${r.pedidoId}`, 'success', false, 0, () => this.cargar());
        },
        error: e => this.mostrarAlerta('Error', e?.error?.message || 'No se pudo procesar el pago', 'error')
      });
      return;
    }

    // USANDO TARJETA NUEVA
    if (!this.nuevaValida) {
      this.mostrarAlerta('Error', 'Revise los datos de la tarjeta', 'error', false);
      return;
    }

    const n = this.formNueva.numero.replace(/\s+/g, '');
    const payload = {
      tokenPasarela: this.svc.simularTokenPasarela(),
      ultimos4: n.slice(-4),
      marca: this.formNueva.marca,
      expiracionMes: Number(this.formNueva.mes),
      expiracionAnio: Number(this.formNueva.anio),
      titular: this.formNueva.titular.trim(),
      guardarTarjeta: !!this.formNueva.guardar
    };

    this.svc.checkout(payload).subscribe({
      next: r => {
        this.showPago = false;
        this.mostrarAlerta('Pago aprobado', `Pedido #${r.pedidoId}`, 'success', false, 0, () => this.cargar());
      },
      error: e => this.mostrarAlerta('Error', e?.error?.message || 'No se pudo procesar el pago', 'error')
    });
  }
}
