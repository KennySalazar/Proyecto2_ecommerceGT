import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GananciasAdminService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/admin/ganancias`;

  obtener(desde: string, hasta: string) {
    return this.http.get<{ total: number }>(this.base, { params: { desde, hasta } });
  }
}