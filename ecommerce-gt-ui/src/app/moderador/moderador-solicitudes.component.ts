import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModeradorService, ModProducto, SpringPage } from './moderador.service';
import { environment } from '../../environments/environment';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../core/auth.service';

@Component({
  standalone:true,
  selector:'app-moderador-solicitudes',
  imports:[CommonModule, FormsModule],
  templateUrl:'./moderador-solicitudes.component.html'
})
export class ModeradorSolicitudesComponent {
  private api = inject(ModeradorService);
  auth = inject(AuthService);
  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');

  lista: ModProducto[] = [];
  pagina=0; tamanio=12; totalPaginas=0;
  cargando=false;

  motivoRechazo=''; showRechazar=false; idRechazo?:number;
  view: 'pendientes' | 'historial' = 'pendientes';

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit(){ this.cargar(); }

  // OBTIENE URL COMPLETA DE LA IMAGEN DEL PRODUCTO
  fullImg(p:ModProducto){ return p.imagenUrl ? this.backendOrigin+p.imagenUrl : null; }

  // CAMBIA ENTRE VISTA DE PENDIENTES E HISTORIAL
  cambiarVista(v: 'pendientes' | 'historial') {
    this.view = v;
    this.pagina = 0;
    this.cargar();
  }

  // CARGA LA LISTA SEGÚN LA VISTA ACTUAL (PENDIENTES O HISTORIAL)
  cargar() {
    this.cargando = true;
    const done = () => (this.cargando = false);

    const obs = this.view === 'pendientes'
      ? this.api.listarPendientes(this.pagina, this.tamanio)
      : this.api.listarHistorial(this.pagina, this.tamanio);

    obs.subscribe({
      next: (pg) => {
        this.lista = pg.content;
        this.totalPaginas = pg.totalPages;
        this.pagina = pg.number;
        done();
      },
      error: () => done()
    });
  }

  // APRUEBA UN PRODUCTO PENDIENTE
  aprobar(p:ModProducto){
    if(!confirm(`Aprobar "${p.nombre}"?`)) return;
    this.api.aprobar(p.id).subscribe(_ => this.cargar());
  }

  // ABRE EL FORMULARIO DE RECHAZO PARA UN PRODUCTO
  abrirRechazo(p:ModProducto){ this.idRechazo = p.id; this.motivoRechazo=''; this.showRechazar=true; }

  // CIERRA EL FORMULARIO DE RECHAZO
  cerrarRechazo(){ this.showRechazar=false; }

  // ENVÍA EL RECHAZO CON MOTIVO AL SERVIDOR
  rechazar(){
    if(!this.idRechazo) return;
    if(!this.motivoRechazo.trim()){ alert('Indica un motivo'); return; }
    this.api.rechazar(this.idRechazo, this.motivoRechazo).subscribe(_ => {
      this.showRechazar=false; this.cargar();
    });
  }

  next(){ if(this.pagina+1<this.totalPaginas){ this.pagina++; this.cargar(); } }

  prev(){ if(this.pagina>0){ this.pagina--; this.cargar(); } }
}
