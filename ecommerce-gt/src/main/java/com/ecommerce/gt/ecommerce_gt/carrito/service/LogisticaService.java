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

/**
 * SERVICIO DE LOGÍSTICA.
 * GESTIONA PEDIDOS EN CURSO, REPROGRAMACIÓN DE ENTREGA Y MARCADO COMO
 * ENTREGADOS.
 * ENVÍA NOTIFICACIONES AL COMPRADOR CUANDO HAY CAMBIOS.
 */
@Service
@RequiredArgsConstructor
public class LogisticaService {

    /** REPOSITORIO DE PEDIDOS */
    private final PedidoRepository pedidoRepo;
    /** REPOSITORIO DE ESTADOS DE PEDIDO */
    private final EstadoPedidoRepository estadoRepo;
    /** REPOSITORIO DE USUARIOS (PARA MOSTRAR NOMBRE DEL COMPRADOR) */
    private final UsuarioRepository usuarioRepo;
    /** SERVICIO DE NOTIFICACIONES AL USUARIO */
    private final NotificacionService noti;

    /**
     * LISTA PEDIDOS EN ESTADO "EN_CURSO".
     *
     * @param page NÚMERO DE PÁGINA (0-BASED)
     * @param size TAMAÑO DE PÁGINA
     * @return PÁGINA DE DTOs CON PEDIDOS EN CURSO
     */
    public Page<PedidoLogisticaDTO> enCurso(int page, int size) {
        var p = PageRequest.of(page, size, Sort.by("id").descending());
        return pedidoRepo.findByEstadoCodigo("EN_CURSO", p).map(this::toDTO);
    }

    /**
     * REPROGRAMA LA FECHA DE ENTREGA DE UN PEDIDO Y NOTIFICA AL COMPRADOR.
     *
     * @param pedidoId   ID DEL PEDIDO
     * @param nuevaFecha NUEVA FECHA DE ENTREGA (YYYY-MM-DD)
     */
    @Transactional
    public void reprogramarEntrega(Integer pedidoId, LocalDate nuevaFecha) {
        var pedido = pedidoRepo.findById(pedidoId).orElseThrow();
        pedido.setFechaEstimadaEntrega(nuevaFecha);
        pedidoRepo.save(pedido);

        // NOTIFICACIÓN AL COMPRADOR SOBRE LA NUEVA FECHA
        var asunto = "NUEVA FECHA DE ENTREGA PARA EL PEDIDO #" + pedido.getId();
        var cuerpo = """
                  Tu pedido #%d fue reprogramado.
                  Nueva fecha estimada de entrega: %s
                """.formatted(pedido.getId(), nuevaFecha);
        noti.crearYEnviar(pedido.getCompradorId(), "PEDIDO_REPROGRAMADO", asunto, cuerpo,
                "{\"pedidoId\":" + pedido.getId() + ",\"nuevaFecha\":\"" + nuevaFecha + "\"}");
    }

    /**
     * MARCA UN PEDIDO COMO "ENTREGADO" Y NOTIFICA AL COMPRADOR.
     *
     * @param pedidoId ID DEL PEDIDO A ACTUALIZAR
     */
    @Transactional
    public void marcarEntregado(Integer pedidoId) {
        var pedido = pedidoRepo.findById(pedidoId).orElseThrow();
        var entregado = estadoRepo.findByCodigo("ENTREGADO").orElseThrow();
        pedido.setEstadoPedido(entregado);
        pedidoRepo.save(pedido);

        // NOTIFICACIÓN AL COMPRADOR SOBRE LA ENTREGA
        var asunto = "TU PEDIDO #" + pedido.getId() + " FUE ENTREGADO";
        var cuerpo = """
                  ¡Listo! Tu pedido #%d fue marcado como:
                  ENTREGADO.
                  Gracias por comprar en E-Commerce GT.
                """.formatted(pedido.getId());
        noti.crearYEnviar(pedido.getCompradorId(), "PEDIDO_ENTREGADO", asunto, cuerpo,
                "{\"pedidoId\":" + pedido.getId() + "}");
    }

    /**
     * CONVIERTE UNA ENTIDAD PEDIDO A DTO PARA LOGÍSTICA.
     * INCLUYE NOMBRE DEL COMPRADOR, FECHAS Y TOTALES.
     *
     * @param p ENTIDAD PEDIDO
     * @return DTO CON DATOS PARA LISTADO DE LOGÍSTICA
     */
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
