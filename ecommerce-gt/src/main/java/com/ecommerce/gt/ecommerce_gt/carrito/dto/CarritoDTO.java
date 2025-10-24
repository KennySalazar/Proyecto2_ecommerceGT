package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CarritoDTO {
    private Integer carritoId;
    private List<CarritoItemDTO> items;
    private Integer total;
}
