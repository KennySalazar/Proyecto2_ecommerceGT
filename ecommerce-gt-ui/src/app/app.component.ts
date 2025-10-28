import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/auth.service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  auth = inject(AuthService);
  router = inject(Router);

  loggedIn = false;
  esComun = false;
  mostrarNavbar = true;

  // SE EJECUTA AL INICIAR LA APLICACIÓN Y DETECTA CAMBIOS DE RUTA
  ngOnInit() {
    this.actualizarEstado();

    // ACTUALIZA EL ESTADO AL CAMBIAR DE RUTA
    this.router.events.pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        this.actualizarEstado();
      });
  }

  // ACTUALIZA ESTADO DE SESIÓN, ROL Y VISIBILIDAD DEL NAVBAR
  private actualizarEstado() {
    const rol = this.auth.obtenerRol();
    this.loggedIn = this.auth.isLoggedIn();
    this.esComun = rol === 'COMUN';

    // OCULTA NAVBAR EN LOGIN Y REGISTER
    const url = this.router.url;
    this.mostrarNavbar = !url.startsWith('/login') && !url.startsWith('/register');
  }
}
