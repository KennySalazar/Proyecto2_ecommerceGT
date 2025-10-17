package com.ecommerce.gt.ecommerce_gt.seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Utilidad para generar y validar JWT.
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMillis = expirationMillis;
    }

    /**
     * Genera un JWT con sujeto (email) y claims adicionales.
     * @param subject correo del usuario
     * @param extraClaims mapa con claims (ej. rol)
     * @return token JWT firmado
     */
    public String generarToken(String subject, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(extraClaims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, Jwts.SIG.HS256) // jjwt 0.11.x
                .compact();
    }

    /**
     * Valida el token (firma y expiración).
     */
    public boolean esValido(String token) {
        try {
            parser().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Obtiene el correo (subject) del token.
     */
    public String getCorreo(String token) {
        return parser().parseSignedClaims(token).getPayload().getSubject();
    }

    /**
     * Obtiene un claim del token.
     */
    public Object getClaim(String token, String claimName) {
        return parser().parseSignedClaims(token).getPayload().get(claimName);
    }

    private JwtParser parser() {
        return Jwts.parser().verifyWith(key).build();
    }
}
