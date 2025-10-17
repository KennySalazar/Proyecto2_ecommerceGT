import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol');
  return rol === 'ADMIN' ? true : router.createUrlTree(['/login']);
};
