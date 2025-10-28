package com.ecommerce.gt.ecommerce_gt.admin;

import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.Map;

/**
 * CONTROLADOR REST PARA CONSULTAR LAS GANANCIAS ADMINISTRATIVAS.
 * PROPORCIONA ENDPOINTS PARA CALCULAR EL TOTAL DE COMISIONES EN UN RANGO DE
 * FECHAS.
 * 
 * RUTA BASE: /api/admin/ganancias
 */
@RestController
@RequestMapping("/api/admin/ganancias")
@RequiredArgsConstructor
public class GananciasAdminController {

    /**
     * REPOSITORIO DE PEDIDOS UTILIZADO PARA CALCULAR LAS COMISIONES.
     */
    private final PedidoRepository pedidoRepo;

    /**
     * OBTIENE EL TOTAL DE GANANCIAS (COMISIONES) GENERADAS EN UN RANGO DE FECHAS.
     * 
     * MÉTODO: GET /api/admin/ganancias
     * 
     * EJEMPLO DE USO:
     * GET /api/admin/ganancias?desde=2025-01-01&hasta=2025-01-31
     *
     * @param desde FECHA DE INICIO DEL RANGO (FORMATO ISO: yyyy-MM-dd).
     * @param hasta FECHA FINAL DEL RANGO (FORMATO ISO: yyyy-MM-dd).
     * @return MAPA CON LA CLAVE "total" Y EL VALOR DE GANANCIA EN LONG.
     *         SI NO HAY RESULTADOS, DEVUELVE 0L.
     */
    @GetMapping
    public Map<String, Long> total(
            @RequestParam String desde,
            @RequestParam String hasta) {

        ZoneId zone = ZoneId.of("America/Guatemala");

        // CONVERSIÓN DE FECHAS A INSTANTES PARA FILTRAR PEDIDOS POR RANGO COMPLETO
        Instant d = LocalDate.parse(desde).atStartOfDay(zone).toInstant();
        Instant h = LocalDate.parse(hasta).plusDays(1).atStartOfDay(zone).toInstant();

        // CONSULTA DEL TOTAL DE COMISIONES EN EL RANGO INDICADO
        Long total = pedidoRepo.sumaComisionPorRango(d, h);

        // RETORNA EL TOTAL EN UN MAPA; SI ES NULO, SE REEMPLAZA POR 0L
        return Map.of("total", (total == null) ? 0L : total);
    }
}
