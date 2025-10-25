package com.ecommerce.gt.ecommerce_gt.carrito.service;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.PedidoLogisticaDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pedido;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.EstadoPedidoRepository;
import com.ecommerce.gt.ecommerce_gt.notificacion.NotificacionService;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LogisticaService {

    private final PedidoRepository pedidoRepo;
    private final EstadoPedidoRepository estadoRepo;
    private final UsuarioRepository usuarioRepo;
    private final NotificacionService noti;

    public Page<PedidoLogisticaDTO> enCurso(int page, int size) {
        var p = PageRequest.of(page, size, Sort.by("id").descending());
        return pedidoRepo.findByEstadoCodigo("EN_CURSO", p).map(this::toDTO);
    }

    @Transactional
    public void reprogramarEntrega(Integer pedidoId, LocalDate nuevaFecha) {
        var pedido = pedidoRepo.findById(pedidoId).orElseThrow();
        pedido.setFechaEstimadaEntrega(nuevaFecha);
        pedidoRepo.save(pedido);
        // Notificar cambio de fecha al comprador
        var asunto = "Nueva fecha de entrega para el pedido #" + pedido.getId();
        var cuerpo = """
                  Tu pedido #%d fue reprogramado.
                  Nueva fecha estimada de entrega: %s
                """.formatted(pedido.getId(), nuevaFecha);
        noti.crearYEnviar(pedido.getCompradorId(), "PEDIDO_REPROGRAMADO", asunto, cuerpo,
                "{\"pedidoId\":" + pedido.getId() + ",\"nuevaFecha\":\"" + nuevaFecha + "\"}");
    }

    @Transactional
    public void marcarEntregado(Integer pedidoId) {
        var pedido = pedidoRepo.findById(pedidoId).orElseThrow();
        var entregado = estadoRepo.findByCodigo("ENTREGADO").orElseThrow();
        pedido.setEstadoPedido(entregado);
        pedidoRepo.save(pedido);

        // Notificar al comprador
        var asunto = "Tu pedido #" + pedido.getId() + " fue ENTREGADO";
        var cuerpo = """
                  ¡Listo! Tu pedido #%d fue marcado como:
                  ENTREGADO.
                  Gracias por comprar en E-Commerce GT.
                """.formatted(pedido.getId());
        noti.crearYEnviar(pedido.getCompradorId(), "PEDIDO_ENTREGADO", asunto, cuerpo,
                "{\"pedidoId\":" + pedido.getId() + "}");
    }

    private PedidoLogisticaDTO toDTO(Pedido p) {
        var comprador = usuarioRepo.findById(p.getCompradorId()).orElse(null);
        var nombre = (comprador != null) ? comprador.getNombre()
                : "Usuario #" + p.getCompradorId();
        return new PedidoLogisticaDTO(
                p.getId(),
                nombre,
                p.getFechaCreacion(),
                p.getFechaEstimadaEntrega(),
                p.getTotalBruto(),
                p.getEstadoPedido().getCodigo());
    }
}