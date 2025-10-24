import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidosService, PedidoResumenDTO } from './pedidos.service';
import { environment } from '../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-mis-compras',
  imports: [CommonModule],
  templateUrl: './mis-compras.component.html'
})
export class MisComprasComponent {
  private api = inject(PedidosService);
  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');

  pedidos: PedidoResumenDTO[] = [];
  cargando = false;

  ngOnInit(){ this.cargar(); }

  cargar() {
    this.cargando = true;
    this.api.misPedidos().subscribe({
      next: d => { this.pedidos = d; this.cargando = false; },
      error: _ => { this.cargando = false; }
    });
  }

  fullImg(url: string | null) {
    return url ? this.backendOrigin + url : null;
  }

  asQ(q: number){
    return q.toLocaleString('es-GT', { style:'currency', currency:'GTQ' });
  }

  fFecha(iso?: string | null){
    if(!iso) return '';
    const d = new Date(iso);
    return d.toLocaleDateString('es-GT', { year:'numeric', month:'short', day:'2-digit' });
  }
}