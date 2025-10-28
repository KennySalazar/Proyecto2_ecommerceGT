import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

// INTERCEPTA TODAS LAS PETICIONES HTTP PARA AÑADIR CABECERAS AUTOMÁTICAMENTE
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token'); // TOKEN JWT GUARDADO EN LOCALSTORAGE

  // VERIFICA SI LA PETICIÓN VA HACIA EL BACKEND
  const isApiCall =
    req.url.startsWith('/api') ||
    (environment.apiBase && req.url.startsWith(environment.apiBase));

  // DETECTA SI LA PETICIÓN ES DE AUTENTICACIÓN (LOGIN, REGISTER, ETC.)
  const isAuthUrl =
    req.url.includes('/auth/') ||
    (environment.apiBase && req.url.startsWith(environment.apiBase + '/auth/'));

  // CABECERAS COMUNES PARA TODAS LAS PETICIONES
  const setHeaders: Record<string, string> = {
    'ngrok-skip-browser-warning': 'true', 
  };

  // AGREGA AUTORIZACIÓN SI EXISTE TOKEN Y NO ES PETICIÓN DE LOGIN
  if (token && isApiCall && !isAuthUrl) {
    setHeaders['Authorization'] = `Bearer ${token}`;
  }

  // CLONA LA PETICIÓN CON LAS CABECERAS NUEVAS Y LA ENVÍA
  req = req.clone({ setHeaders });
  return next(req);
};
