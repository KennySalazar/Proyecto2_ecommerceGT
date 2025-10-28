package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import com.ecommerce.gt.ecommerce_gt.notificacion.dto.NotificacionDTO;

/**
 * CONTROLADOR DE NOTIFICACIONES.
 * EXPONE ENDPOINTS PARA:
 * - SUSCRIBIRSE A NOTIFICACIONES EN TIEMPO REAL
 * - CONSULTAR NOTIFICACIONES DEL USUARIO
 *
 * RUTA BASE: /api/notificaciones
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    /** HUB QUE ADMINISTRA LAS SUSCRIPCIONES SSE Y EMITE EVENTOS. */
    private final NotificacionHub hub;

    /** REPOSITORIO PARA LEER NOTIFICACIONES GUARDADAS EN BD. */
    private final NotificacionRepository repo;

    /** SERVICIO PARA MAPEAR ENTIDADES A DTO Y LÓGICA DE NOTIFICACIONES. */
    private final NotificacionService service;

    /** UTILIDAD JWT PARA OBTENER EL ID DE USUARIO DESDE EL TOKEN. */
    private final JwtUtil jwt;

    /**
     * ABRE UN STREAM SSE (EVENTSOURCE) PARA RECIBIR NOTIFICACIONES EN TIEMPO REAL.
     *
     * MÉTODO: GET /api/notificaciones/stream?token=Bearer%20XXX
     *
     * @param rawToken TOKEN TAL COMO LLEGA
     * @return SseEmitter LISTO PARA ENVIAR EVENTOS AL CLIENTE.
     */
    @GetMapping("/stream")
    public SseEmitter stream(@RequestParam("token") String rawToken) {
        Integer userId = jwt.getUserIdFromHeader(rawToken);
        return hub.suscribir(userId);
    }

    /**
     * DEVUELVE LAS NOTIFICACIONES DEL USUARIO AUTENTICADO.
     *
     * MÉTODO: GET /api/notificaciones/mias
     *
     * @param auth     CABECERA "Authorization" CON EL TOKEN JWT.
     * @param sinceIso FECHA/HORA
     * @return LISTA DE NotificacionDTO ORDENADA SEGÚN EL CASO.
     */
    @GetMapping("/mias")
    public List<NotificacionDTO> mias(
            @RequestHeader("Authorization") String auth,
            @RequestParam(value = "since", required = false) String sinceIso) {

        Integer userId = jwt.getUserIdFromHeader(auth);

        // SIN PARÁMETRO SINCE: ÚLTIMAS 20 NOTIFICACIONES
        if (sinceIso == null || sinceIso.isBlank()) {
            return repo.findTop20ByUsuarioIdOrderByCreadoEnDesc(userId)
                    .stream()
                    .map(service::mapearDTO)
                    .collect(Collectors.toList());
        }

        // CON PARÁMETRO SINCE: NOTIFICACIONES DESPUÉS DE ESA FECHA
        var since = Instant.parse(sinceIso);
        return repo.findByUsuarioIdAndCreadoEnAfterOrderByCreadoEnAsc(userId, since)
                .stream()
                .map(service::mapearDTO)
                .collect(Collectors.toList());
    }
}
