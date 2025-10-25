import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GananciasAdminService } from './ganancias-admin.service';

@Component({
  standalone: true,
  selector: 'app-ganancias-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './ganancias-admin.component.html'
})
export class GananciasAdminComponent {
  private api = inject(GananciasAdminService);

  desde = this.hoy();
  hasta = this.hoy();
  total = 0;

  ngOnInit(){ this.filtrar(); }

  filtrar(){
    this.api.obtener(this.desde, this.hasta).subscribe({
      next: r => this.total = r.total ?? 0,
      error: _ => this.total = 0
    });
  }

  private hoy(): string {
    const d = new Date();
    return d.toISOString().substring(0,10);
  }
}