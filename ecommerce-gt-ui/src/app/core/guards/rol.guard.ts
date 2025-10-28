import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../auth.service';

// VERIFICA SI EL USUARIO TIENE PERMISOS PARA ACCEDER A UNA RUTA SEGÚN SU ROL
export const RolGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const auth   = inject(AuthService);

  const permitido: string[] = route.data?.['roles'] ?? []; // ROLES PERMITIDOS EN LA RUTA
  const rol = auth.obtenerRol();                           // ROL ACTUAL DEL USUARIO

  // SI EL ROL ESTÁ PERMITIDO, SE AUTORIZA EL ACCESO
  if (rol && permitido.includes(rol)) return true;

  // SI NO ESTÁ PERMITIDO, REDIRIGE SEGÚN SU ROL
  switch (rol) {
    case 'ADMIN':     router.navigateByUrl('/admin/empleados'); break;
    case 'MODERADOR': router.navigateByUrl('/moderador/solicitudes'); break;
    case 'LOGISTICA': router.navigateByUrl('/logistica/pendientes'); break;
    default:          router.navigateByUrl('/inicio'); break;
  }

  return false; // BLOQUEA EL ACCESO A LA RUTA ORIGINAL
};
