import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

// GUARD PARA PROTEGER LAS RUTAS DE ADMINISTRADOR
export const adminGuard: CanActivateFn = () => {
  // SE INYECTA EL ROUTER PARA PODER REDIRIGIR
  const router = inject(Router);

  // SE OBTIENE EL ROL DEL USUARIO DESDE EL LOCAL STORAGE
  const rol = localStorage.getItem('rol');

  // SI EL ROL ES ADMIN, PERMITE EL ACCESO
  // SI NO, REDIRIGE AL LOGIN
  return rol === 'ADMIN' ? true : router.createUrlTree(['/login']);
};
