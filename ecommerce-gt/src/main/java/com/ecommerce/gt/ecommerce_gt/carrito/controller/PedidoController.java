package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.PedidoItemLiteDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.PedidoResumenDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoItemRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoImagenRepository;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * CONTROLADOR DE PEDIDOS DEL USUARIO COMPRADOR.
 * PERMITE CONSULTAR LOS PEDIDOS REALIZADOS POR EL USUARIO AUTENTICADO,
 * JUNTO CON SUS PRODUCTOS Y DETALLES BÁSICOS.
 * 
 * RUTA BASE: /api/pedidos
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    // REPOSITORIO PARA CONSULTAR PEDIDOS
    private final PedidoRepository pedidoRepo;

    // REPOSITORIO PARA CONSULTAR LOS ITEMS DE CADA PEDIDO
    private final PedidoItemRepository itemRepo;

    // REPOSITORIO PARA OBTENER LAS IMÁGENES DE LOS PRODUCTOS
    private final ProductoImagenRepository imgRepo;

    // UTILIDAD PARA EXTRAER DATOS DEL TOKEN JWT
    private final JwtUtil jwt;

    /**
     * OBTIENE LA LISTA DE PEDIDOS DEL USUARIO AUTENTICADO.
     * CADA PEDIDO INCLUYE SUS PRODUCTOS Y DATOS RESUMIDOS.
     *
     * MÉTODO: GET /api/pedidos/mios
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL USUARIO.
     * @return LISTA DE PedidoResumenDTO CON INFORMACIÓN DE LOS PEDIDOS DEL USUARIO.
     */
    @GetMapping("/mios")
    public List<PedidoResumenDTO> misPedidos(@RequestHeader("Authorization") String auth) {
        // SE OBTIENE EL ID DEL USUARIO DESDE EL TOKEN
        Integer userId = jwt.getUserIdFromHeader(auth);

        // SE BUSCAN LOS PEDIDOS DEL USUARIO ORDENADOS POR FECHA (DESCENDENTE)
        var pedidos = pedidoRepo.findByCompradorIdOrderByFechaCreacionDesc(userId);

        // SE TRANSFORMA CADA PEDIDO EN UN OBJETO PedidoResumenDTO
        return pedidos.stream().map(p -> {

            // PARA CADA PEDIDO, SE OBTIENEN SUS ITEMS (PRODUCTOS)
            List<PedidoItemLiteDTO> items = itemRepo.findByPedidoId(p.getId()).stream().map((PedidoItem it) -> {
                var prod = it.getProducto();

                // SE BUSCA LA IMAGEN PRINCIPAL DEL PRODUCTO
                var img = imgRepo.findFirstByProductoIdOrderByIdDesc(prod.getId()).orElse(null);
                String imgUrl = (img != null) ? ("/uploads/" + img.getUrl()) : null;

                // SE CREA UN OBJETO CON DATOS SIMPLIFICADOS DEL PRODUCTO
                return new PedidoItemLiteDTO(
                        prod.getId(),
                        prod.getNombre(),
                        imgUrl,
                        it.getCantidad(),
                        it.getPrecioUnitario(),
                        it.getSubtotal());
            }).toList();

            // SE CREA EL RESUMEN DEL PEDIDO CON DATOS PRINCIPALES Y SUS ITEMS
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
