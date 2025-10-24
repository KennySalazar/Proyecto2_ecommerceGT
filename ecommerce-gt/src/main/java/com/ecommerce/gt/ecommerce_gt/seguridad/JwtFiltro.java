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
 * Filtro que extrae el JWT del header Authorization y, si es válido,
 * coloca la autenticación en el SecurityContext con la autoridad del rol.
 */
@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFiltro(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (auth != null && auth.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = auth.substring(7).trim();

            if (jwtUtil.esValido(token)) {
                String correo = jwtUtil.getCorreo(token);
                Object rolObj = jwtUtil.getClaim(token, "rol");
                String rol = (rolObj == null) ? "COMUN" : rolObj.toString().trim();
                if (rol.startsWith("ROLE_"))
                    rol = rol.substring("ROLE_".length());
                rol = rol.toUpperCase();

                var authToken = new UsernamePasswordAuthenticationToken(
                        correo, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
