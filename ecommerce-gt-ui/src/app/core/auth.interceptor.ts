// src/app/core/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // ⚠️ No agregar Authorization a endpoints /auth/**
  const isAuthUrl =
    req.url.includes('/auth/') ||
    (environment.apiBase && req.url.startsWith(environment.apiBase + '/auth/'));

  if (token && !isAuthUrl) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(req);
};
