package com.ecommerce.gt.ecommerce_gt.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * FILTRO JWT PARA CADA PETICIÓN.
 * LEE EL TOKEN DEL HEADER AUTHORIZATION, LO VALIDA
 * Y COLOCA LA AUTENTICACIÓN EN EL CONTEXTO DE SEGURIDAD.
 */
@Component
public class JwtFiltro extends OncePerRequestFilter {

    /** UTILIDAD PARA VALIDAR Y LEER DATOS DEL JWT */
    private final JwtUtil jwtUtil;

    /**
     * CONSTRUCTOR CON JwtUtil.
     *
     * @param jwtUtil UTILIDAD PARA OPERAR CON EL TOKEN
     */
    public JwtFiltro(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * EJECUTA EL FILTRO:
     * 1) LEE EL HEADER AUTHORIZATION ("BEARER <TOKEN>").
     * 2) SI EL TOKEN ES VÁLIDO Y NO HAY AUTENTICACIÓN PREVIA, OBTIENE CORREO Y ROL.
     * 3) CREA EL AUTH TOKEN CON LA AUTORIDAD "ROLE_<ROL>".
     * 4) GUARDA LA AUTENTICACIÓN EN EL SecurityContext.
     * 5) CONTINÚA CON LA CADENA DE FILTROS.
     *
     * @param request     SOLICITUD HTTP
     * @param response    RESPUESTA HTTP
     * @param filterChain CADENA DE FILTROS
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        // VERIFICA QUE HAYA TOKEN BEARER Y QUE AÚN NO EXISTA AUTENTICACIÓN EN EL
        // CONTEXTO
        if (auth != null && auth.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = auth.substring(7).trim();

            // VALIDA EL TOKEN Y EXTRAE DATOS BÁSICOS
            if (jwtUtil.esValido(token)) {
                String correo = jwtUtil.getCorreo(token);
                Object rolObj = jwtUtil.getClaim(token, "rol");

                // NORMALIZA EL ROL A FORMATO MAYÚSCULAS SIN PREFIJO
                String rol = (rolObj == null) ? "COMUN" : rolObj.toString().trim();
                if (rol.startsWith("ROLE_"))
                    rol = rol.substring("ROLE_".length());
                rol = rol.toUpperCase();

                // CREA LA AUTENTICACIÓN CON LA AUTORIDAD "ROLE_<ROL>"
                var authToken = new UsernamePasswordAuthenticationToken(
                        correo, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
