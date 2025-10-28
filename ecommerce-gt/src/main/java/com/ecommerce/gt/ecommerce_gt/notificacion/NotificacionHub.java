package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * HUB CENTRAL DE NOTIFICACIONES EN TIEMPO REAL (SSE).
 * MANTIENE UNA LISTA DE CONEXIONES ACTIVAS (EMITTERS) POR USUARIO
 * Y ENVÍA EVENTOS CUANDO SE GENERAN NUEVAS NOTIFICACIONES.
 */
@Component
public class NotificacionHub {

    /** MAPA DE USUARIOS SUSCRITOS Y SUS CONEXIONES ACTIVAS SSE. */
    private final Map<Integer, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * SUSCRIBE A UN USUARIO AL STREAM DE NOTIFICACIONES.
     * CREA UN NUEVO SseEmitter Y LO AGREGA A SU LISTA DE CONEXIONES ACTIVAS.
     *
     * @param userId ID DEL USUARIO A SUSCRIBIR
     * @return NUEVO EMISOR SSE LISTO PARA ENVIAR EVENTOS
     */
    public SseEmitter suscribir(Integer userId) {
        var em = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, ignored -> new CopyOnWriteArrayList<>()).add(em);

        // CUANDO EL CLIENTE SE DESCONECTA, LIMPIA LA CONEXIÓN
        em.onCompletion(() -> remove(userId, em));
        em.onTimeout(() -> remove(userId, em));
        em.onError(ex -> remove(userId, em));

        return em;
    }

    /**
     * ENVÍA UNA NOTIFICACIÓN SSE A UN USUARIO ESPECÍFICO.
     * SI ALGUNA CONEXIÓN FALLA (EJ. CLIENTE DESCONECTADO), SE ELIMINA
     * AUTOMÁTICAMENTE.
     *
     * @param userId  ID DEL USUARIO DESTINATARIO
     * @param payload OBJETO A ENVIAR COMO CONTENIDO DE LA NOTIFICACIÓN
     */
    public void emitir(Integer userId, Object payload) {
        var list = emitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
        list.forEach(em -> {
            try {
                em.send(SseEmitter.event().name("notificacion").data(payload));
            } catch (IOException e) {
                remove(userId, em);
            }
        });
    }

    /**
     * ELIMINA UN EMISOR SSE DE LA LISTA DE CONEXIONES DE UN USUARIO.
     * SE EJECUTA AUTOMÁTICAMENTE CUANDO LA CONEXIÓN TERMINA O FALLA.
     *
     * @param userId ID DEL USUARIO
     * @param em     EMISOR A ELIMINAR
     */
    private void remove(Integer userId, SseEmitter em) {
        var list = emitters.get(userId);
        if (list != null)
            list.remove(em);
    }
}
