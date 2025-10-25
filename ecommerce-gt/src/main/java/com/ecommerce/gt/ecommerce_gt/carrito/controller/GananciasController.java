package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.GananciaResumenDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.GananciaItemDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.service.GananciasService;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ganancias")
@RequiredArgsConstructor
public class GananciasController {
    private final GananciasService svc;
    private final JwtUtil jwt;

    @GetMapping("/resumen")
    public GananciaResumenDTO resumen(@RequestHeader("Authorization") String auth) {
        Integer vendedorId = jwt.getUserIdFromHeader(auth);
        return svc.resumen(vendedorId);
    }

    @GetMapping
    public Page<GananciaItemDTO> listar(@RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        Integer vendedorId = jwt.getUserIdFromHeader(auth);
        return svc.listar(vendedorId, pagina, tamanio);
    }
}