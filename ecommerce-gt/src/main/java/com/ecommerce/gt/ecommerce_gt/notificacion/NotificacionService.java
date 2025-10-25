package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.time.Instant;

import com.ecommerce.gt.ecommerce_gt.notificacion.dto.NotificacionDTO;
import com.ecommerce.gt.ecommerce_gt.notificacion.entity.Notificacion;
import com.ecommerce.gt.ecommerce_gt.comun.EmailService;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacionService {
    private final NotificacionRepository repo;
    private final UsuarioRepository usuarios;
    private final EmailService email;
    private final NotificacionHub hub;

    @Transactional
    public Notificacion crearYEnviar(Integer usuarioId, String tipo, String asunto, String cuerpo,
            String metadataJson) {
        var n = new Notificacion();
        n.setUsuarioId(usuarioId);
        n.setTipo(tipo);
        n.setAsunto(asunto);
        n.setCuerpo(cuerpo);
        n = repo.save(n);

        usuarios.findById(usuarioId).ifPresent(u -> {
            email.enviar(u.getCorreo(), asunto, cuerpo);
        });
        n.setEnviado(true);
        n.setEnviadoEn(Instant.now());
        repo.save(n);

        hub.emitir(usuarioId, mapearDTO(n));
        return n;
    }

    public NotificacionDTO mapearDTO(Notificacion n) {
        return new NotificacionDTO(
                n.getId(),
                n.getTipo(),
                n.getAsunto(),
                n.getCuerpo(),
                n.getCreadoEn().toString(),
                Boolean.TRUE.equals(n.getEnviadoEn()));
    }

}