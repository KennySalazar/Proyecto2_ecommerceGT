// com/ecommerce/gt/ecommerce_gt/admin/reportes/ReportesAdminController.java
package com.ecommerce.gt.ecommerce_gt.admin.reportes;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.*;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoItemReportesRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoReportesRepository;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoReportesRepository;
import com.ecommerce.gt.ecommerce_gt.notificacion.NotificacionReportesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reportes")
@RequiredArgsConstructor
public class ReportesAdminController {

    private final PedidoItemReportesRepository itemRepo;
    private final PedidoReportesRepository pedidoRepo;
    private final ProductoReportesRepository productoRepo;
    private final NotificacionReportesRepository notiRepo;

    private Instant startOf(LocalDate d) {
        return d.atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant nextDay(LocalDate d) {
        return d.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant atStart(String iso) {
        var zone = ZoneId.of("America/Guatemala");
        return LocalDate.parse(iso).atStartOfDay(zone).toInstant();
    }

    private Instant nextDay(String iso) {
        var zone = ZoneId.of("America/Guatemala");
        return LocalDate.parse(iso).plusDays(1).atStartOfDay(zone).toInstant();
    }

    @GetMapping("/top-productos")
    public List<ProductoTopDTO> topProductos(
            @RequestParam String desde, @RequestParam String hasta,
            @RequestParam(defaultValue = "10") int limit) {
        return itemRepo.topProductos(atStart(desde), nextDay(hasta), PageRequest.of(0, limit));
    }

    @GetMapping("/top-clientes-ganancia")
    public List<VendedorNetoDTO> topClientesGanancia(
            @RequestParam String desde, @RequestParam String hasta,
            @RequestParam(defaultValue = "5") int limit) {
        return pedidoRepo.topVendedoresNeto(atStart(desde), nextDay(hasta), PageRequest.of(0, limit));
    }

    @GetMapping("/top-vendedores")
    public List<TopClienteCantidadDTO> topVendedores(
            @RequestParam String desde, @RequestParam String hasta,
            @RequestParam(defaultValue = "5") int limit) {
        return itemRepo.topVendedoresPorUnidades(atStart(desde), nextDay(hasta), PageRequest.of(0, limit));
    }

    @GetMapping("/top-clientes-pedidos")
    public List<TopClienteCantidadDTO> topClientesPorPedidos(
            @RequestParam String desde, @RequestParam String hasta,
            @RequestParam(defaultValue = "10") int limit) {
        return pedidoRepo.topClientesPorPedidos(atStart(desde), nextDay(hasta), PageRequest.of(0, limit));
    }

    @GetMapping("/top-inventario")
    public List<TopClienteCantidadDTO> topInventario(
            @RequestParam(defaultValue = "10") int limit) {
        return productoRepo.topInventarioVendedores(PageRequest.of(0, limit));
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionRowDTO>> notificaciones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        var res = notiRepo.historial(startOf(desde), nextDay(hasta));
        return ResponseEntity.ok(res);
    }
}
