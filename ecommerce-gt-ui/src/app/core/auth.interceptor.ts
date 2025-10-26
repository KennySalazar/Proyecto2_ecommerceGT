import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // ¿Es una llamada a la API?
  const isApiCall =
    req.url.startsWith('/api') ||
    (environment.apiBase && req.url.startsWith(environment.apiBase));

  // ¿Es endpoint de auth?
  const isAuthUrl =
    req.url.includes('/auth/') ||
    (environment.apiBase && req.url.startsWith(environment.apiBase + '/auth/'));

  const setHeaders: Record<string, string> = {
    // Bypass del aviso de ngrok
    'ngrok-skip-browser-warning': 'true',
  };

  if (token && isApiCall && !isAuthUrl) {
    setHeaders['Authorization'] = `Bearer ${token}`;
  }

  req = req.clone({ setHeaders });
  return next(req);
};
