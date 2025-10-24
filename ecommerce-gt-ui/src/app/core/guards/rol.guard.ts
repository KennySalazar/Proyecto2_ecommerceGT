import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../auth.service';

export const RolGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const auth   = inject(AuthService);

  const permitido: string[] = route.data?.['roles'] ?? [];
  const rol = auth.obtenerRol();

  if (rol && permitido.includes(rol)) return true;

  
  switch (rol) {
    case 'ADMIN':     router.navigateByUrl('/admin/empleados'); break;
    case 'MODERADOR': router.navigateByUrl('/moderador/solicitudes'); break;
    case 'LOGISTICA': router.navigateByUrl('/logistica/pendientes'); break;
    default:          router.navigateByUrl('/inicio'); break;
  }
  return false;
};
