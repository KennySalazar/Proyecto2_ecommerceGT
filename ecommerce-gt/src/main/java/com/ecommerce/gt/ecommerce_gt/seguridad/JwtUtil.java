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
 * UTILIDAD PARA MANEJAR JWT.
 * GENERA TOKENS, LOS VALIDA Y LEE SUS DATOS
 * COMPATIBLE CON JJWT 0.11.5.
 */
@Component
public class JwtUtil {

    /** LLAVE SECRETA PARA FIRMAR Y VALIDAR EL TOKEN */
    private final Key key;
    /** TIEMPO DE EXPIRACIÓN DEL TOKEN (MILISEGUNDOS) */
    private final long expirationMillis;

    /**
     * CONSTRUYE LA UTILIDAD CARGANDO LA LLAVE Y LA EXPIRACIÓN DESDE PROPIEDADES.
     *
     * @param secretBase64     SECRETO EN BASE64
     * @param expirationMillis MILISEGUNDOS DE VIDA DEL TOKEN
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.expiration-ms:86400000}") long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expirationMillis = expirationMillis;
    }

    /**
     * GENERA UN TOKEN JWT CON UN SUBJECT (CORREO) Y CLAIMS (ROL, ID).
     *
     * @param subject     CORREO O IDENTIFICADOR DEL USUARIO
     * @param extraClaims MAPA DE CLAIMS ADICIONALES
     * @return TOKEN JWT EN TEXTO
     */
    public String generarToken(String subject, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * VERIFICA QUE EL TOKEN TENGA FIRMA VÁLIDA Y NO ESTÉ VENCIDO.
     *
     * @param token TOKEN JWT
     * @return TRUE SI ES VÁLIDO; FALSE SI NO
     */
    public boolean esValido(String token) {
        try {
            parser().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * OBTIENE EL SUBJECT (CORREO) DEL TOKEN.
     *
     * @param token TOKEN JWT
     * @return CORREO DEL USUARIO
     */
    public String getCorreo(String token) {
        return parser().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * LEE UN CLAIM ESPECÍFICO DEL TOKEN.
     *
     * @param token     TOKEN JWT
     * @param claimName NOMBRE DEL CLAIM
     * @return VALOR DEL CLAIM O NULL
     */
    public Object getClaim(String token, String claimName) {
        return parser().parseClaimsJws(token).getBody().get(claimName);
    }

    /**
     * CREA EL PARSER CONFIGURADO CON LA LLAVE DE FIRMA.
     *
     * @return JwtParser LISTO PARA USARSE
     */
    private JwtParser parser() {
        return Jwts.parserBuilder().setSigningKey(key).build();
    }

    /**
     * OBTIENE EL USER ID DESDE UN HEADER O TOKEN.
     * BUSCA EL CLAIM "id" PRIMERO.
     *
     * @param authHeaderOrToken HEADER "AUTHORIZATION" O EL TOKEN PURO
     * @return ID DE USUARIO COMO ENTERO
     */
    public Integer getUserId(String authHeaderOrToken) {
        String token = resolveToken(authHeaderOrToken);
        Object val = getClaim(token, "id");
        if (val instanceof Number n)
            return n.intValue();
        throw new IllegalStateException("EL TOKEN NO CONTIENE EL CLAIM 'id' VÁLIDO");
    }

    /**
     * OBTIENE EL ROL DESDE UN HEADER O TOKEN.
     *
     * @param authHeaderOrToken HEADER O TOKEN
     * @return ROL COMO STRING O NULL
     */
    public String getRol(String authHeaderOrToken) {
        String token = resolveToken(authHeaderOrToken);
        Object val = getClaim(token, "rol");
        return val != null ? val.toString() : null;
    }

    /**
     * OBTIENE EL CORREO DESDE UN HEADER O TOKEN.
     *
     * @param authHeaderOrToken HEADER O TOKEN
     * @return CORREO DEL SUBJECT
     */
    public String getCorreoFromAuth(String authHeaderOrToken) {
        String token = resolveToken(authHeaderOrToken);
        return getCorreo(token);
    }

    /**
     * EXTRAE EL TOKEN PURO.
     * SI RECIBE "BEARER XXX", DEVUELVE "XXX".
     *
     * @param authHeaderOrToken HEADER AUTHORIZATION O TOKEN
     * @return TOKEN SIN PREFIJO
     */
    public String resolveToken(String authHeaderOrToken) {
        if (authHeaderOrToken == null) {
            throw new IllegalArgumentException("AUTHORIZATION HEADER/TOKEN ES NULL");
        }
        String t = authHeaderOrToken.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }

    /**
     * LEE UN CLAIM ENTERO MANEJANDO TIPOS VARIADOS.
     *
     * @param token TOKEN JWT
     * @param claim NOMBRE DEL CLAIM
     * @return ENTERO O NULL SI NO SE PUEDE PARSEAR
     */
    public Integer getIntegerClaim(String token, String claim) {
        Object v = getClaim(token, claim);
        if (v == null)
            return null;
        if (v instanceof Integer i)
            return i;
        if (v instanceof Number n)
            return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * OBTIENE EL USER ID DESDE LA CABECERA "AUTHORIZATION".
     * BUSCA PRIMERO "userId" Y LUEGO "id".
     *
     * @param authHeader CABECERA AUTHORIZATION "BEARER <TOKEN>"
     * @return ID DE USUARIO O NULL SI NO EXISTE/NO ES NÚMERO
     */
    public Integer getUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        String token = authHeader.substring(7).trim();

        Object id = getClaim(token, "userId");
        if (id == null)
            id = getClaim(token, "id");
        if (id == null)
            return null;

        try {
            return Integer.parseInt(id.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
