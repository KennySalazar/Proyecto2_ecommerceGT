import { Component, OnInit, inject,  OnDestroy} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmpleadosService, EmpleadoFila, SpringPage } from './empleados.service';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { AuthService } from '../core/auth.service';

@Component({
  standalone: true,
  selector: 'app-empleados-listar',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './empleados-listar.component.html',
})

export class EmpleadosListarComponent implements OnInit, OnDestroy {
  private service = inject(EmpleadosService);

  auth = inject(AuthService);
  adminNombre: string = '';

  empleados: EmpleadoFila[] = [];
  pagina = 0;
  tamanio = 10;
  total = 0;
  totalPaginas = 0;

  
  nombre = '';
  rol: 'TODOS'|'ADMIN'|'MODERADOR'|'LOGISTICA'|'COMUN' = 'TODOS';

  
  private nombreInput$ = new Subject<string>();
  private destroy$ = new Subject<void>();

  ngOnInit() {
    this.cargar();

   
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

  
  onNombreChange(valor: string) {
    this.nombreInput$.next(valor);
  }

  
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

  buscar() {           
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
