package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import com.ecommerce.gt.ecommerce_gt.notificacion.dto.NotificacionDTO;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    private final NotificacionHub hub;
    private final NotificacionRepository repo;
    private final NotificacionService service;
    private final JwtUtil jwt;

    @GetMapping("/stream")
    public SseEmitter stream(@RequestParam("token") String rawToken) {
        Integer userId = jwt.getUserIdFromHeader(rawToken);
        return hub.suscribir(userId);
    }

    @GetMapping("/mias")
    public List<NotificacionDTO> mias(
            @RequestHeader("Authorization") String auth,
            @RequestParam(value = "since", required = false) String sinceIso) {

        Integer userId = jwt.getUserIdFromHeader(auth);

        if (sinceIso == null || sinceIso.isBlank()) {
            return repo.findTop20ByUsuarioIdOrderByCreadoEnDesc(userId)
                    .stream()
                    .map(service::mapearDTO)
                    .collect(Collectors.toList());
        }
        var since = Instant.parse(sinceIso);
        return repo.findByUsuarioIdAndCreadoEnAfterOrderByCreadoEnAsc(userId, since)
                .stream()
                .map(service::mapearDTO)
                .collect(Collectors.toList());
    }
}
