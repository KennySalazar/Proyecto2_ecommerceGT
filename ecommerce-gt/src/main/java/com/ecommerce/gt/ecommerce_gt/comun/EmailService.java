package com.ecommerce.gt.ecommerce_gt.comun;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * SERVICIO DE ENVÍO DE CORREOS ELECTRÓNICOS.
 * PERMITE ENVIAR MENSAJES EN FORMATO TEXTO O HTML DE MANERA ASÍNCRONA.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    /** CLIENTE DE ENVÍO DE CORREOS CONFIGURADO EN SPRING */
    private final JavaMailSender mail;

    /** NOMBRE QUE APARECERÁ COMO REMITENTE */
    private static final String FROM_NAME = "E-Commerce GT";
    /** CORREO DE REMITENTE CONFIGURADO PARA LA APLICACIÓN */
    private static final String FROM_EMAIL = "sistemasss25@gmail.com";

    /**
     * ENVÍA UN CORREO SIMPLE (TEXTO PLANO).
     * SE USA COMO MÉTODO DE RESPALDO O PARA NOTIFICACIONES BÁSICAS.
     *
     * @param para   DIRECCIÓN DE CORREO DESTINATARIO
     * @param asunto ASUNTO DEL MENSAJE
     * @param texto  CUERPO DEL MENSAJE EN TEXTO PLANO
     */
    @Async
    public void enviar(String para, String asunto, String texto) {
        try {
            var msg = new SimpleMailMessage();
            msg.setTo(para);
            msg.setSubject(asunto);
            msg.setText(texto);
            msg.setFrom(String.format("%s <%s>", FROM_NAME, FROM_EMAIL));
            mail.send(msg);
        } catch (Exception e) {
            System.err.println("No se pudo enviar correo a " + para + ": " + e.getMessage());
        }
    }

    /**
     * ENVÍA UN CORREO CON CONTENIDO HTML.
     *
     * @param para   DIRECCIÓN DE CORREO DESTINATARIO
     * @param asunto ASUNTO DEL MENSAJE
     * @param html   CUERPO DEL MENSAJE EN FORMATO HTML
     */
    @Async
    public void enviarHtml(String para, String asunto, String html) {
        try {
            MimeMessage mime = mail.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setFrom(String.format("%s <%s>", FROM_NAME, FROM_EMAIL));
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mail.send(mime);
        } catch (MessagingException e) {
            // SI FALLA EL ENVÍO HTML, SE ENVÍA COMO TEXTO PLANO SIN ETIQUETAS
            enviar(para, asunto, html.replaceAll("<[^>]+>", ""));
        }
    }
}
