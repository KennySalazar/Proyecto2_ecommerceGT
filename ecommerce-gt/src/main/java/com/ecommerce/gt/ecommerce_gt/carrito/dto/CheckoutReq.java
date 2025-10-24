package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutReq {
    private Integer tarjetaGuardadaId;
    private String tokenPasarela;
    private String ultimos4;
    private String marca;
    private Integer expiracionMes;
    private Integer expiracionAnio;
    private String titular;
    private Boolean guardarTarjeta;
}
