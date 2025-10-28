import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GananciasAdminService } from './ganancias-admin.service';
import { RouterLink } from '@angular/router';

// COMPONENTE PARA MOSTRAR LAS GANANCIAS DEL ADMINISTRADOR
@Component({
  standalone: true,
  selector: 'app-ganancias-admin',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './ganancias-admin.component.html'
})
export class GananciasAdminComponent {
  
  private api = inject(GananciasAdminService);

  // FECHAS DE FILTRO 
  desde = this.hoy();
  hasta = this.hoy();

  // VARIABLE PARA GUARDAR EL TOTAL DE GANANCIAS
  total = 0;

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit(){ 
    this.filtrar(); 
  }

  // OBTIENE LAS GANANCIAS SEGÚN EL RANGO DE FECHAS
  filtrar(){
    this.api.obtener(this.desde, this.hasta).subscribe({
      next: r => this.total = r.total ?? 0, 
      error: _ => this.total = 0             
    });
  }

  // FUNCION PARA OBTENER LA FECHA ACTUAL EN FORMATO YYYY-MM-DD
  private hoy(): string {
    const d = new Date();
    return d.toISOString().substring(0,10);
  }
}
