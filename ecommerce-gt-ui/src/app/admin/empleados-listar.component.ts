import { Component, OnInit, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmpleadosService, EmpleadoFila, SpringPage } from './empleados.service';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { AuthService } from '../core/auth.service';

// COMPONENTE PARA LISTAR LOS EMPLEADOS
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

  // DATOS DE LOS EMPLEADOS Y PAGINACIÓN
  empleados: EmpleadoFila[] = [];
  pagina = 0;
  tamanio = 10;
  total = 0;
  totalPaginas = 0;

  // FILTROS DE BÚSQUEDA
  nombre = '';
  rol: 'TODOS' | 'ADMIN' | 'MODERADOR' | 'LOGISTICA' | 'COMUN' = 'TODOS';

  // SUBJECT PARA MANEJAR CAMBIOS EN EL NOMBRE 
  private nombreInput$ = new Subject<string>();

  // SUBJECT PARA LIMPIAR SUSCRIPCIONES AL DESTRUIR EL COMPONENTE
  private destroy$ = new Subject<void>();

  // SE EJECUTA AL INICIAR EL COMPONENTE
  ngOnInit() {
    this.cargar();

    // SE CONFIGURA LA ESCUCHA DEL INPUT 
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

  // SE EJECUTA AL DESTRUIR EL COMPONENTE PARA EVITAR MEMORIA OCUPADA
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // SE LLAMA CUANDO EL USUARIO ESCRIBE EN EL CAMPO NOMBRE
  onNombreChange(valor: string) {
    this.nombreInput$.next(valor);
  }

  // SE LLAMA CUANDO CAMBIA EL ROL SELECCIONADO
  onRolChange() {
    this.pagina = 0;
    this.cargar();
  }

  // CARGA LOS EMPLEADOS DESDE EL SERVICIO CON LOS FILTROS ACTUALES
  cargar() {
    this.service.listar(this.pagina, this.tamanio, { nombre: this.nombre, rol: this.rol })
      .subscribe((page: SpringPage<EmpleadoFila>) => {
        // ASIGNA LOS DATOS RECIBIDOS DEL BACKEND
        this.empleados = page.content;
        this.total = page.totalElements;
        this.totalPaginas = page.totalPages;
        this.pagina = page.number;
      });
  }

  // INICIA UNA NUEVA BÚSQUEDA DESDE LA PRIMERA PÁGINA
  buscar() {           
    this.pagina = 0;
    this.cargar();
  }

  // LIMPIA LOS FILTROS Y RECARGA LOS DATOS
  limpiar() {
    this.nombre = '';
    this.rol = 'TODOS';
    this.buscar();
  }

  // PASA A LA SIGUIENTE PÁGINA SI EXISTE
  next() {
    if (this.pagina + 1 < this.totalPaginas) {
      this.pagina++;
      this.cargar();
    }
  }

  // RETROCEDE UNA PÁGINA SI NO ESTÁ EN LA PRIMERA
  prev() {
    if (this.pagina > 0) {
      this.pagina--;
      this.cargar();
    }
  }
}
