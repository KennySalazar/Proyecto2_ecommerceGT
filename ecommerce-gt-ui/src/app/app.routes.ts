import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { adminGuard } from './admin/admin.guard';
import { InicioComponent } from './core/inicio.component';
import { EmpleadosListarComponent } from './admin/empleados-listar.component';
import { EmpleadoCrearComponent } from './admin/empleado-crear.component';
import { CatalogoComponent } from './comun/catalogo.component';
import { MisProductosComponent } from './comun/mis-productos.component';
import { ProductoFormComponent } from './comun/producto-form.component';
import { CarritoComponent } from './comun/carrito.component';
import { RolGuard } from './core/guards/rol.guard';
import { MisComprasComponent } from './comun/mis-compras.component';
import { ModeradorSolicitudesComponent } from './moderador/moderador-solicitudes.component';
import { MisGananciasComponent } from './comun/mis-ganancias.component';
import { LogisticaPendientesComponent } from './logistica/logistica-pendientes.component';
import { GananciasAdminComponent } from './admin/ganancias-admin.component';

export const routes: Routes = [
  // RUTAS PÚBLICAS
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // CATÁLOGO PRINCIPAL (USUARIO COMÚN)
  { path: 'inicio', component: CatalogoComponent },

  // SECCIÓN ADMINISTRADOR (PROTEGIDA POR GUARD)
  {
    path: 'admin',
    canMatch: [adminGuard],
    children: [
      { path: 'empleados', component: EmpleadosListarComponent },
      { path: 'empleados/crear', component: EmpleadoCrearComponent },
      { path: '', redirectTo: 'empleados', pathMatch: 'full' },
    ]
  },

  // SECCIÓN DE PRODUCTOS DEL USUARIO
  { path: 'mis-productos', component: MisProductosComponent },
  { path: 'mis-productos/nuevo', component: ProductoFormComponent },
  { path: 'mis-productos/:id/editar', component: ProductoFormComponent },

  // CARRITO Y COMPRAS
  { path: 'carrito', component: CarritoComponent },
  { path: 'mis-compras', component: MisComprasComponent },

  // PANEL DEL MODERADOR
  { path: 'moderador/solicitudes', component: ModeradorSolicitudesComponent },

  // PANEL DE LOGÍSTICA
  { path: 'logistica/pendientes', component: LogisticaPendientesComponent },

  // ADMIN: EDICIÓN DE EMPLEADOS Y GANANCIAS
  { path: 'admin/empleados/:id/editar', component: EmpleadoCrearComponent },
  { path: 'mis-ganancias', component: MisGananciasComponent },
  { path: 'admin/ganancias', component: GananciasAdminComponent },

  // ADMIN: REPORTES (CARGA PEREZOSA)
  { path: 'admin/reportes', loadComponent: () => import('./admin/admin-reportes.component').then(m => m.AdminReportesComponent) },

  // REDIRECCIONES POR DEFECTO
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' }
];
