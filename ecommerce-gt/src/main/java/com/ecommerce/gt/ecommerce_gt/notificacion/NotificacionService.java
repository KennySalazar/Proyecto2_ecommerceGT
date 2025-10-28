package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.time.Instant;

import com.ecommerce.gt.ecommerce_gt.notificacion.dto.NotificacionDTO;
import com.ecommerce.gt.ecommerce_gt.notificacion.entity.Notificacion;
import com.ecommerce.gt.ecommerce_gt.comun.EmailService;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SERVICIO DE NOTIFICACIONES.
 * CREA NOTIFICACIONES, INTENTA ENVIARLAS POR CORREO
 * Y LAS EMITE EN TIEMPO REAL VIA SSE.
 */
@Service
@RequiredArgsConstructor
public class NotificacionService {

    /** REPOSITORIO PARA GUARDAR Y LEER NOTIFICACIONES */
    private final NotificacionRepository repo;

    /** REPOSITORIO DE USUARIOS (PARA OBTENER CORREO DEL DESTINATARIO) */
    private final UsuarioRepository usuarios;

    /** SERVICIO DE CORREO PARA ENVIAR LAS NOTIFICACIONES */
    private final EmailService email;

    /** HUB SSE PARA ENVIAR NOTIFICACIONES EN TIEMPO REAL AL CLIENTE */
    private final NotificacionHub hub;

    /**
     * CREA UNA NOTIFICACIÓN, LA GUARDA, INTENTA ENVIAR CORREO
     * Y EMITE EL EVENTO EN TIEMPO REAL.
     *
     * @param usuarioId ID DEL USUARIO DESTINATARIO
     * @param tipo      TIPO DE NOTIFICACIÓN
     * @param asunto    ASUNTO DEL MENSAJE
     * @param cuerpo    CUERPO DEL MENSAJE
     * @return ENTIDAD Notificacion PERSISTIDA
     */
    @Transactional
    public Notificacion crearYEnviar(Integer usuarioId, String tipo, String asunto, String cuerpo,
            String metadataJson) {
        // CREAR Y GUARDAR LA NOTIFICACIÓN
        var n = new Notificacion();
        n.setUsuarioId(usuarioId);
        n.setTipo(tipo);
        n.setAsunto(asunto);
        n.setCuerpo(cuerpo);
        n = repo.save(n);

        // ENVIAR CORREO SI EXISTE EL USUARIO
        usuarios.findById(usuarioId).ifPresent(u -> {
            email.enviar(u.getCorreo(), asunto, cuerpo);
        });

        // MARCAR COMO ENVIADA Y REGISTRAR FECHA/HORA DE ENVÍO
        n.setEnviado(true);
        n.setEnviadoEn(Instant.now());
        repo.save(n);

        // EMITIR POR SSE AL CLIENTE SUSCRITO
        hub.emitir(usuarioId, mapearDTO(n));
        return n;
    }

    /**
     * CONVIERTE UNA ENTIDAD Notificacion A DTO PARA EL CLIENTE.
     *
     * @param n ENTIDAD NOTIFICACIÓN
     * @return NotificacionDTO CON CAMPOS BÁSICOS
     */
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
