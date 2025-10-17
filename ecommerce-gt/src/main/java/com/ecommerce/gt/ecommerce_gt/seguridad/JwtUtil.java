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
 * Utilidad para generar y validar JWT (compatible con jjwt 0.11.5).
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.expiration-ms:86400000}") long expirationMillis
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expirationMillis = expirationMillis;
    }

    /** Genera un token JWT con el subject (correo) y claims extra (p.ej. rol). */
    public String generarToken(String subject, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, SignatureAlgorithm.HS256)   // 0.11.5 usa SignatureAlgorithm
                .compact();
    }

    /** Valida firma y expiración del token. */
    public boolean esValido(String token) {
        try {
            parser().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Obtiene el correo (subject) del token. */
    public String getCorreo(String token) {
        return parser().parseClaimsJws(token).getBody().getSubject();
    }

    /** Obtiene un claim por nombre (String). */
    public Object getClaim(String token, String claimName) {
        return parser().parseClaimsJws(token).getBody().get(claimName);
    }

    private JwtParser parser() {
        // En 0.11.5 se usa parserBuilder() + setSigningKey(...)
        return Jwts.parserBuilder().setSigningKey(key).build();
    }
}
