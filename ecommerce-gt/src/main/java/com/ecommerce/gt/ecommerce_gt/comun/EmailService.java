package com.ecommerce.gt.ecommerce_gt.comun;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mail;

    private static final String FROM_NAME = "E-Commerce GT";
    private static final String FROM_EMAIL = "sistemasss25@gmail.com";

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

            enviar(para, asunto, html.replaceAll("<[^>]+>", ""));
        }
    }
}