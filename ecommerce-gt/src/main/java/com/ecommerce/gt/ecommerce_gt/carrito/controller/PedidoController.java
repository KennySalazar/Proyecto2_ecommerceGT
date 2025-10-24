package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.PedidoItemLiteDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.PedidoResumenDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoItemRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoImagenRepository;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepo;
    private final PedidoItemRepository itemRepo;
    private final ProductoImagenRepository imgRepo;
    private final JwtUtil jwt;

    @GetMapping("/mios")
    public List<PedidoResumenDTO> misPedidos(@RequestHeader("Authorization") String auth) {
        Integer userId = jwt.getUserIdFromHeader(auth);

        var pedidos = pedidoRepo.findByCompradorIdOrderByFechaCreacionDesc(userId);

        return pedidos.stream().map(p -> {
            List<PedidoItemLiteDTO> items = itemRepo.findByPedidoId(p.getId()).stream().map((PedidoItem it) -> {
                var prod = it.getProducto();
                var img = imgRepo.findFirstByProductoIdOrderByIdDesc(prod.getId()).orElse(null);
                String imgUrl = (img != null) ? ("/uploads/" + img.getUrl()) : null;

                return new PedidoItemLiteDTO(
                        prod.getId(),
                        prod.getNombre(),
                        imgUrl,
                        it.getCantidad(),
                        it.getPrecioUnitario(),
                        it.getSubtotal());
            }).toList();

            return new PedidoResumenDTO(
                    p.getId(),
                    p.getFechaCreacion(),
                    p.getTotalBruto(),
                    p.getEstadoPedido().getDescripcion(),
                    p.getFechaEstimadaEntrega(),
                    items);
        }).toList();
    }
}