package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.PedidoLogisticaDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.service.LogisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/logistica")
@RequiredArgsConstructor
public class LogisticaController {

    private final LogisticaService service;

    @GetMapping("/en-curso")
    public Page<PedidoLogisticaDTO> enCurso(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        return service.enCurso(pagina, tamanio);
    }

    @PutMapping("/{pedidoId}/fecha-entrega")
    public void actualizarFecha(@PathVariable Integer pedidoId,
            @RequestParam("fecha") String isoDate) {
        service.reprogramarEntrega(pedidoId, LocalDate.parse(isoDate));
    }

    @PostMapping("/{pedidoId}/entregar")
    public void entregar(@PathVariable Integer pedidoId) {
        service.marcarEntregado(pedidoId);
    }
}
