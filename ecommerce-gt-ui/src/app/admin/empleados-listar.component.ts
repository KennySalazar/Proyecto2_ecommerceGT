// src/app/admin/empleados-listar.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmpleadosService, EmpleadoFila } from './empleados.service';

@Component({
  selector: 'app-empleados-listar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './empleados-listar.component.html'
})
export class EmpleadosListarComponent implements OnInit {
  private svc = inject(EmpleadosService);

  empleados: EmpleadoFila[] = [];
  pagina = 0;
  totalPaginas = 0;

  ngOnInit() {
    this.cargar(0);
  }

  cargar(p: number) {
    this.svc.listar(p, 10).subscribe({
      next: (resp) => {
        this.empleados = resp.content;   // <- content
        this.pagina = resp.number;       // <- number
        this.totalPaginas = resp.totalPages;
      },
      error: (e) => console.error('Error listando empleados', e)
    });
  }

  prev() { if (this.pagina > 0) this.cargar(this.pagina - 1); }
  next() { if (this.pagina + 1 < this.totalPaginas) this.cargar(this.pagina + 1); }
}
