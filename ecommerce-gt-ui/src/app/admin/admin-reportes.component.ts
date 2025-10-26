import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  ReportesService,
  ProductoTopDTO,
  TopClienteCantidadDTO,
  topVendedoresDTO,
  Page,
  NotificacionRowDTO
} from './reportes.service';
import { RouterLink } from '@angular/router';

function hoyISO(): string {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${dd}`;
}

@Component({
  standalone: true,
  selector: 'app-admin-reportes',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-reportes.component.html'
})
export class AdminReportesComponent {
  private api = inject(ReportesService);

  desde = hoyISO();
  hasta = hoyISO();

  tab: 'productos' | 'ganancia' | 'vendedores' | 'pedidos' | 'inventario' | 'notis' = 'productos';

  topProductos: ProductoTopDTO[] = [];
  topGanancia: topVendedoresDTO[] = [];
  topVendedores: TopClienteCantidadDTO[] = [];
  topPedidos: TopClienteCantidadDTO[] = [];
  topInventario: TopClienteCantidadDTO[] = [];
  notisSimple: NotificacionRowDTO[] = [];
  cargandoNotisSimple = false;

  n_desde = '';
  n_hasta = '';
  n_usuarioId?: number;
  n_tipo = '';
  n_page?: Page<any>;
  n_pagina = 0;
  n_tamanio = 20;
  cargandoNotis = false;

  cargando = false;
  error = '';

  ngOnInit() { this.cargarTodo(); }

  setTab(t: typeof this.tab){ this.tab = t; }

  cargarTodo(){
    this.cargando = true;
    this.error = '';

    Promise.all([
      this.api.topProductos(this.desde, this.hasta, 10).toPromise(),
      this.api.topClientesGanancia(this.desde, this.hasta, 5).toPromise(),
      this.api.topVendedores(this.desde, this.hasta, 5).toPromise(),
      this.api.topClientesPedidos(this.desde, this.hasta, 10).toPromise(),
      this.api.topInventario(10).toPromise(),
      this.cargarNotificacionesSimple()
    ]).then(([prod, gan, vend, ped, inv]) => {
      this.topProductos = prod ?? [];
      this.topGanancia = gan ?? [];
      this.topVendedores = vend ?? [];
      this.topPedidos = ped ?? [];
      this.topInventario = inv ?? [];
    }).catch(_ => {
      this.error = 'No se pudieron cargar los reportes.';
    }).finally(() => this.cargando = false);
  }

    cargarNotificacionesSimple(){
    this.cargandoNotisSimple = true;
    this.api.notificaciones(this.desde, this.hasta).subscribe({
      next: r => { this.notisSimple = r ?? []; this.cargandoNotisSimple = false; },
      error: _ => { this.cargandoNotisSimple = false; }
    });
  }

  limpiarNotis(){
    this.n_desde = ''; this.n_hasta = ''; this.n_usuarioId = undefined; this.n_tipo = '';
    this.cargarNotificacionesSimple();
  }

  onTabNotis() {
  this.setTab('notis');
  if (!this.n_page) {
    this.cargarNotificacionesSimple();
  }
}
}
