import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-inicio',
  imports: [CommonModule],
  template: `
  <div class="container py-4">
    <div class="d-flex align-items-center gap-2 mb-3">
      <i class="bi bi-house-door fs-3 text-primary"></i>
      <h3 class="m-0">Inicio del Usuario</h3>
    </div>
    <p>Bienvenido al marketplace. Aquí podrás ver productos destacados, tu carrito, etc.</p>
  </div>
  `
})
export class InicioComponent {}
