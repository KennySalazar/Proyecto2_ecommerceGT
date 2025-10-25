package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class NotificacionHub {
    // conexiones SSE
    private final Map<Integer, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter suscribir(Integer userId) {
        var em = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, ignored -> new CopyOnWriteArrayList<>()).add(em);

        em.onCompletion(() -> remove(userId, em));
        em.onTimeout(() -> remove(userId, em));
        em.onError(ex -> remove(userId, em));
        return em;
    }

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

    private void remove(Integer userId, SseEmitter em) {
        var list = emitters.get(userId);
        if (list != null)
            list.remove(em);
    }
}