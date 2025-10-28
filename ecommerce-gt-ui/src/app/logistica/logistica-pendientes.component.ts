import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LogisticaService, PedidoLogisticaDTO, SpringPage } from './logistica.service';
import { AuthService } from '../core/auth.service';

@Component({
  standalone:true,
  selector:'app-logistica-pendientes',
  imports:[CommonModule, FormsModule],
  templateUrl:'./logistica-pendientes.component.html'
})
export class LogisticaPendientesComponent {
  private api = inject(LogisticaService);
  auth = inject(AuthService);

  lista: PedidoLogisticaDTO[] = [];
  pagina=0; tamanio=12; totalPaginas=0; cargando=false;

  editId?: number;
  nuevaFecha = '';

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit(){ this.cargar(); }

  // CARGA LOS PEDIDOS EN CURSO PARA LOGÍSTICA
  cargar(){
    this.cargando = true;
    this.api.enCurso(this.pagina, this.tamanio).subscribe({
      next: (p: SpringPage<PedidoLogisticaDTO>) => {
        this.lista = p.content; this.totalPaginas = p.totalPages; this.pagina = p.number;
        this.cargando = false;
      },
      error: _ => this.cargando = false
    });
  }

  // ABRE EL CAMPO PARA EDITAR FECHA DE ENTREGA DE UN PEDIDO
  abrirFecha(p:PedidoLogisticaDTO){
    this.editId = p.id;
    this.nuevaFecha = (p.fechaEstimadaEntrega ?? '').substring(0,10); 
  }

  // GUARDA LA NUEVA FECHA DE ENTREGA EN EL SERVIDOR
  guardarFecha(){
    if(!this.editId || !this.nuevaFecha) return;
    this.api.actualizarFechaEntrega(this.editId, this.nuevaFecha).subscribe({
      next: _ => { this.editId = undefined; this.cargar(); },
      error: _ => alert('No se pudo reprogramar la entrega')
    });
  }

  // MARCA UN PEDIDO COMO ENTREGADO
  entregar(p:PedidoLogisticaDTO){
    if(!confirm(`Marcar como ENTREGADO el pedido #${p.id}?`)) return;
    this.api.marcarEntregado(p.id).subscribe({
      next: _ => this.cargar(),
      error: _ => alert('No se pudo marcar como entregado')
    });
  }

  // AVANZA UNA PÁGINA EN LA LISTA
  next(){ if(this.pagina+1<this.totalPaginas){ this.pagina++; this.cargar(); } }

  // RETROCEDE UNA PÁGINA EN LA LISTA
  prev(){ if(this.pagina>0){ this.pagina--; this.cargar(); } }
}
