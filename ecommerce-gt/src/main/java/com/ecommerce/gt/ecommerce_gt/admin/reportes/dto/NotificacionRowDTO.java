package com.ecommerce.gt.ecommerce_gt.admin.reportes.dto;

import java.time.Instant;

public record NotificacionRowDTO(
        Integer id,
        Integer usuarioId,
        String usuarioNombre,
        String tipo,
        String asunto,
        Boolean enviado,
        Instant enviadoEn,
        Instant creadoEn) {
}