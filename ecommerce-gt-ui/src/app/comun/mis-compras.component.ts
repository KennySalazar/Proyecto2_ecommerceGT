import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidosService, PedidoResumenDTO } from './pedidos.service';
import { environment } from '../../environments/environment';
import { ImgUrlPipe } from '../shared/pipes/img-url.pipe';

@Component({
  standalone: true,                        
  selector: 'app-mis-compras',              
  imports: [CommonModule, ImgUrlPipe],      
  templateUrl: './mis-compras.component.html' 
})
export class MisComprasComponent {
  private api = inject(PedidosService);     
  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, ''); // URL BASE DEL BACKEND

  pedidos: PedidoResumenDTO[] = [];        
  cargando = false;                         

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit(){ this.cargar(); }

  // CARGA TODOS LOS PEDIDOS DEL USUARIO
  cargar() {
    this.cargando = true;
    this.api.misPedidos().subscribe({
      next: d => { this.pedidos = d; this.cargando = false; }, 
      error: _ => { this.cargando = false; }                   
    });
  }

  // OBTIENE URL COMPLETA DE IMAGEN O DEVUELVE IMAGEN POR DEFECTO
  fullImg(url: string | null): string {
    if (!url) return 'assets/noimg.png';
    return url.startsWith('/uploads/') ? url : '/uploads/' + url.replace(/^\/+/, '');
  }

  
  asQ(q: number){
    return q.toLocaleString('es-GT', { style:'currency', currency:'GTQ' });
  }

  // FORMATEA DE FECHA
  fFecha(iso?: string | null){
    if(!iso) return '';
    const d = new Date(iso);
    return d.toLocaleDateString('es-GT', { year:'numeric', month:'short', day:'2-digit' });
  }
}
