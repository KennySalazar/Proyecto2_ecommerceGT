import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GananciasService, GananciaResumenDTO, GananciaItemDTO, Page } from './ganancias.service';
import { environment } from '../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-mis-ganancias',
  imports: [CommonModule],
  templateUrl: './mis-ganancias.component.html'
})
export class MisGananciasComponent {
  private api = inject(GananciasService);

  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');
  r?: GananciaResumenDTO;
  lista: GananciaItemDTO[] = [];
  pagina = 0; tamanio = 12; totalPaginas = 0;
  cargando = false;

  ngOnInit(){ this.cargar(); }

  fullImg(u?: string|null){ return u ? this.backendOrigin + u : null; }
  asQ(n:number){ return n.toLocaleString('es-GT',{style:'currency',currency:'GTQ'}) }
  fFecha(iso:string){ const d = new Date(iso); return d.toLocaleDateString('es-GT',{year:'numeric',month:'short',day:'2-digit'})}

  cargar(){
    this.cargando = true;
    this.api.resumen().subscribe(res => this.r = res);
    this.api.listar(this.pagina, this.tamanio).subscribe((p: Page<GananciaItemDTO>) => {
      this.lista = p.content;
      this.totalPaginas = p.totalPages;
      this.pagina = p.number;
      this.cargando = false;
    }, _ => this.cargando=false);
  }

  next(){ if(this.pagina+1<this.totalPaginas){ this.pagina++; this.cargar(); } }
  prev(){ if(this.pagina>0){ this.pagina--; this.cargar(); } }
}