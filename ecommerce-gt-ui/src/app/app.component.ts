import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  auth = inject(AuthService);

 
  get loggedIn(): boolean {
    
    return !!this.auth.token;
  }

  get esComun(): boolean {
    return this.auth.obtenerRol() === 'COMUN';
  }
}
