import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GananciasService, GananciaResumenDTO, GananciaItemDTO, Page } from './ganancias.service';
import { environment } from '../../environments/environment';
import { ImgUrlPipe } from '../shared/pipes/img-url.pipe';

@Component({
  standalone: true,
  selector: 'app-mis-ganancias',
  imports: [CommonModule, ImgUrlPipe],
  templateUrl: './mis-ganancias.component.html'
})
export class MisGananciasComponent {
  private api = inject(GananciasService);

  backendOrigin = environment.backendOrigin ?? environment.apiBase.replace(/\/api\/?$/, '');
  r?: GananciaResumenDTO;
  lista: GananciaItemDTO[] = [];
  pagina = 0; tamanio = 12; totalPaginas = 0;
  cargando = false;

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit(){ this.cargar(); }

  // OBTIENE URL COMPLETA DE IMAGEN O DEVUELVE IMAGEN POR DEFECTO
  fullImg(u?: string | null): string {
    const url = u ?? null;
    if (!url) return 'assets/noimg.png';
    return url.startsWith('/uploads/') ? url : '/uploads/' + url.replace(/^\/+/, '');
  }

  asQ(n:number){ return n.toLocaleString('es-GT',{style:'currency',currency:'GTQ'}) }

  // FORMATEA FECHA 
  fFecha(iso:string){ const d = new Date(iso); return d.toLocaleDateString('es-GT',{year:'numeric',month:'short',day:'2-digit'})}

  // CARGA RESUMEN Y LISTA DE GANANCIAS DESDE EL BACKEND
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

  // AVANZA UNA PÁGINA EN LA LISTA
  next(){ if(this.pagina+1<this.totalPaginas){ this.pagina++; this.cargar(); } }

  // RETROCEDE UNA PÁGINA EN LA LISTA
  prev(){ if(this.pagina>0){ this.pagina--; this.cargar(); } }
}
