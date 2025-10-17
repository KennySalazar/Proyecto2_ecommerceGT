// src/app/admin/empleados-listar.component.ts
import { Component, OnInit, inject,  OnDestroy} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmpleadosService, EmpleadoFila, SpringPage } from './empleados.service';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

@Component({
  standalone: true,
  selector: 'app-empleados-listar',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './empleados-listar.component.html',
})

export class EmpleadosListarComponent implements OnInit, OnDestroy {
  private service = inject(EmpleadosService);

  empleados: EmpleadoFila[] = [];
  pagina = 0;
  tamanio = 10;
  total = 0;
  totalPaginas = 0;

  // filtros
  nombre = '';
  rol: 'TODOS'|'ADMIN'|'MODERADOR'|'LOGISTICA'|'COMUN' = 'TODOS';

  // 👇 subjects para el debounce y destrucción
  private nombreInput$ = new Subject<string>();
  private destroy$ = new Subject<void>();

  ngOnInit() {
    this.cargar();

    // cuando el usuario escribe, esperamos 300ms y filtramos
    this.nombreInput$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe((texto) => {
        this.nombre = texto;
        this.pagina = 0;
        this.cargar();
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // llamado por el (ngModelChange) del input
  onNombreChange(valor: string) {
    this.nombreInput$.next(valor);
  }

  // llamado por el (change) del select rol
  onRolChange() {
    this.pagina = 0;
    this.cargar();
  }

  cargar() {
    this.service.listar(this.pagina, this.tamanio, { nombre: this.nombre, rol: this.rol })
      .subscribe((page: SpringPage<EmpleadoFila>) => {
        this.empleados = page.content;
        this.total = page.totalElements;
        this.totalPaginas = page.totalPages;
        this.pagina = page.number;
      });
  }

  buscar() {            // (si dejas el botón "Buscar")
    this.pagina = 0;
    this.cargar();
  }

  limpiar() {
    this.nombre = '';
    this.rol = 'TODOS';
    this.buscar();
  }

  next() {
    if (this.pagina + 1 < this.totalPaginas) {
      this.pagina++;
      this.cargar();
    }
  }

  prev() {
    if (this.pagina > 0) {
      this.pagina--;
      this.cargar();
    }
  }
}
