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


export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Inicio usuario común
   { path: 'inicio', component: CatalogoComponent },

  // ADMIN
   {
    path: 'admin',
    canMatch: [adminGuard],
    children: [
      { path: 'empleados', component: EmpleadosListarComponent },
      { path: 'empleados/crear', component: EmpleadoCrearComponent },
      { path: '', redirectTo: 'empleados', pathMatch: 'full' },
    ]
  },

  { path: 'mis-productos', component: MisProductosComponent},
  { path: 'mis-productos/nuevo', component: ProductoFormComponent },
  { path: 'mis-productos/:id/editar', component: ProductoFormComponent },

   { path: 'carrito', component: CarritoComponent },
   { path: 'mis-compras', component: MisComprasComponent },

  

  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' }
];
