package com.ecommerce.gt.ecommerce_gt.admin;

import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ganancias")
@RequiredArgsConstructor
public class GananciasAdminController {

    private final PedidoRepository pedidoRepo;

    @GetMapping
    public Map<String, Long> total(
            @RequestParam String desde, // yyyy-MM-dd
            @RequestParam String hasta) { // yyyy-MM-dd
        ZoneId zone = ZoneId.of("America/Guatemala");
        Instant d = LocalDate.parse(desde).atStartOfDay(zone).toInstant();
        Instant h = LocalDate.parse(hasta).plusDays(1).atStartOfDay(zone).toInstant();
        Long total = pedidoRepo.sumaComisionPorRango(d, h);
        return Map.of("total", (total == null) ? 0L : total);
    }
}