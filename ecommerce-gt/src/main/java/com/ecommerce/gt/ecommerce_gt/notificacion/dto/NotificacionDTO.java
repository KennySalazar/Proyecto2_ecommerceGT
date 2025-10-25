package com.ecommerce.gt.ecommerce_gt.notificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificacionDTO {
    private Integer id;
    private String tipo;
    private String asunto;
    private String cuerpo;
    private String creadoEnIso;
    private boolean leida;
}
