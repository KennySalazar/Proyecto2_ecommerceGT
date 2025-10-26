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
  mostrarNavbar = true; // controlará si mostramos o no la barra de usuario

  ngOnInit() {
    this.actualizarEstado();

    // Detecta navegación entre rutas
    this.router.events.pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        this.actualizarEstado();
      });
  }

  private actualizarEstado() {
    const rol = this.auth.obtenerRol();
    this.loggedIn = this.auth.isLoggedIn();
    this.esComun = rol === 'COMUN';

    // Oculta menú si estás en login o register
    const url = this.router.url;
    this.mostrarNavbar = !url.startsWith('/login') && !url.startsWith('/register');
  }
}