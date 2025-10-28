package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.PedidoLogisticaDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.service.LogisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * CONTROLADOR DE LOGÍSTICA.
 * MANEJA LAS OPERACIONES RELACIONADAS CON LA GESTIÓN DE PEDIDOS
 * EN ETAPA DE ENVÍO Y ENTREGA.
 * 
 * RUTA BASE: /api/logistica
 */
@RestController
@RequestMapping("/api/logistica")
@RequiredArgsConstructor
public class LogisticaController {

    // SERVICIO QUE CONTIENE LA LÓGICA DE NEGOCIO PARA LA GESTIÓN LOGÍSTICA
    private final LogisticaService service;

    /**
     * LISTA LOS PEDIDOS QUE SE ENCUENTRAN EN CURSO (AÚN NO ENTREGADOS).
     * PERMITE PAGINAR LOS RESULTADOS.
     *
     * MÉTODO: GET /api/logistica/en-curso
     *
     * @param pagina  NÚMERO DE PÁGINA A MOSTRAR (POR DEFECTO 0).
     * @param tamanio TAMAÑO DE PÁGINA (POR DEFECTO 12).
     * @return PÁGINA DE PedidoLogisticaDTO CON LOS PEDIDOS EN CURSO.
     */
    @GetMapping("/en-curso")
    public Page<PedidoLogisticaDTO> enCurso(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        return service.enCurso(pagina, tamanio);
    }

    /**
     * ACTUALIZA LA FECHA DE ENTREGA PROGRAMADA DE UN PEDIDO.
     *
     * MÉTODO: PUT /api/logistica/{pedidoId}/fecha-entrega?fecha=YYYY-MM-DD
     *
     * @param pedidoId ID DEL PEDIDO A MODIFICAR.
     * @param isoDate  NUEVA FECHA DE ENTREGA EN FORMATO ISO (YYYY-MM-DD).
     */
    @PutMapping("/{pedidoId}/fecha-entrega")
    public void actualizarFecha(@PathVariable Integer pedidoId,
            @RequestParam("fecha") String isoDate) {
        service.reprogramarEntrega(pedidoId, LocalDate.parse(isoDate));
    }

    /**
     * MARCA UN PEDIDO COMO ENTREGADO.
     * CAMBIA SU ESTADO EN EL SISTEMA A "ENTREGADO".
     *
     * MÉTODO: POST /api/logistica/{pedidoId}/entregar
     *
     * @param pedidoId ID DEL PEDIDO QUE SE VA A MARCAR COMO ENTREGADO.
     */
    @PostMapping("/{pedidoId}/entregar")
    public void entregar(@PathVariable Integer pedidoId) {
        service.marcarEntregado(pedidoId);
    }
}
