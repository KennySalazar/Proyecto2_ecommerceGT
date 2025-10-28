package com.ecommerce.gt.ecommerce_gt.carrito.service;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.*;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.*;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoImagenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

/**
 * SERVICIO DE GANANCIAS PARA VENDEDORES.
 * CALCULA RESÚMENES RÁPIDOS Y LISTAS PAGINADAS DE VENTAS.
 */
@Service
@RequiredArgsConstructor
public class GananciasService {

    /** REPOSITORIO DE ITEMS DE PEDIDO */
    private final PedidoItemRepository itemRepo;

    /** REPOSITORIO DE IMÁGENES DE PRODUCTO */
    private final ProductoImagenRepository imgRepo;

    /**
     * DEVUELVE UN RESUMEN DE GANANCIAS DEL VENDEDOR.
     * INCLUYE TOTAL HISTÓRICO, HOY, MES, Y CANTIDAD DE VENTAS.
     *
     * @param vendedorId ID DEL VENDEDOR
     * @return DTO CON TOTALES Y CONTADORES
     */
    public GananciaResumenDTO resumen(Integer vendedorId) {
        var hoy = LocalDate.now();
        var iniHoy = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant();
        var finHoy = hoy.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        var iniMes = hoy.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        var finMes = hoy.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        long totalNeto = itemRepo.sumNetoTotal(vendedorId);
        long netoHoy = itemRepo.sumNetoRango(vendedorId, iniHoy, finHoy);
        long netoMes = itemRepo.sumNetoRango(vendedorId, iniMes, finMes);

        int ventasHoy = itemRepo.countRango(vendedorId, iniHoy, finHoy);
        int ventasMes = itemRepo.countRango(vendedorId, iniMes, finMes);

        return new GananciaResumenDTO(totalNeto, netoHoy, netoMes, ventasHoy, ventasMes);
    }

    /**
     * LISTA LAS VENTAS (ITEMS) DEL VENDEDOR EN FORMATO PAGINADO.
     * MAPEa CADA ITEM A UN DTO CON DATOS DEL PEDIDO Y DEL PRODUCTO.
     *
     * @param vendedorId ID DEL VENDEDOR
     * @param pagina     NÚMERO DE PÁGINA (0-BASED)
     * @param tamanio    TAMAÑO DE LA PÁGINA
     * @return PÁGINA DE GananciaItemDTO
     */
    public Page<GananciaItemDTO> listar(Integer vendedorId, int pagina, int tamanio) {
        Pageable p = PageRequest.of(pagina, tamanio);
        long total = itemRepo.countByVendedor(vendedorId);
        List<PedidoItem> rows = itemRepo.findAllByVendedor(vendedorId, p);

        var dtos = rows.stream().map(pi -> {
            var pr = pi.getProducto();
            var ped = pi.getPedido();

            var img = imgRepo.findFirstByProductoIdOrderByIdDesc(pr.getId()).orElse(null);
            String url = (img != null) ? "/uploads/" + img.getUrl() : null;

            return new GananciaItemDTO(
                    ped.getId(),
                    ped.getFechaCreacion(),
                    pr.getId(),
                    pr.getNombre(),
                    pi.getCantidad(),
                    pi.getPrecioUnitario(),
                    pi.getSubtotal(),
                    pi.getComision(),
                    pi.getNetoVendedor(),
                    ped.getEstadoPedido().getDescripcion(),
                    url);
        }).toList();

        return new PageImpl<>(dtos, p, total);
    }
}
