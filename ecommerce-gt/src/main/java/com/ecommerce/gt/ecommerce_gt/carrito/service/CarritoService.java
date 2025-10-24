package com.ecommerce.gt.ecommerce_gt.carrito.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.CarritoDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.CarritoItemDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.CheckoutReq;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.Carrito;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.CarritoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.CarritoItemPK;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.EstadoPedido;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pago;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pedido;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItemPK;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.TarjetaGuardada;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.CarritoItemRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.CarritoRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.EstadoPedidoRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PagoRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoItemRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.TarjetaGuardadaRepository;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoRepository;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoImagenRepository;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepo;
    private final CarritoItemRepository itemRepo;
    private final ProductoRepository productoRepo;
    private final ProductoImagenRepository imgRepo;
    private final TarjetaGuardadaRepository tarjetaRepo;
    private final PedidoRepository pedidoRepo;
    private final PedidoItemRepository pedidoItemRepo;
    private final EstadoPedidoRepository estadoPedidoRepo;
    private final JwtUtil jwt;
    private final PagoRepository pagoRepo;

    /** Obtiene el carrito vigente del usuario o lo crea */
    private Carrito getOrCreate(Integer usuarioId) {
        return carritoRepo.findFirstByUsuarioIdAndEstaVigenteTrueOrderByIdDesc(usuarioId)
                .orElseGet(() -> {
                    var c = new Carrito();
                    c.setUsuario(new Usuario(usuarioId));
                    return carritoRepo.save(c);
                });
    }

    /** Mapea a DTO incluyendo stockDisponible */
    private CarritoDTO mapDTO(Carrito c) {
        var items = itemRepo.findByCarritoId(c.getId()).stream().map(ci -> {
            var p = ci.getProducto();

            var principal = imgRepo.findFirstByProductoIdOrderByIdDesc(p.getId()).orElse(null);
            String imgUrl = (principal != null) ? ("/uploads/" + principal.getUrl()) : null;

            Integer restante = productoRepo.stockRestante(p.getId());

            return new CarritoItemDTO(
                    p.getId(),
                    p.getNombre(),
                    imgUrl,
                    ci.getPrecio(),
                    ci.getCantidad(),
                    ci.getPrecio() * ci.getCantidad(),
                    restante);
        }).toList();

        int total = items.stream().mapToInt(CarritoItemDTO::getSubtotal).sum();
        return new CarritoDTO(c.getId(), items, total);
    }

    public CarritoDTO ver(Integer usuarioId) {
        var c = getOrCreate(usuarioId);

        var items = itemRepo.findByCarritoId(c.getId()).stream().map(ci -> {
            var p = ci.getProducto();
            var principal = imgRepo.findFirstByProductoIdOrderByIdDesc(p.getId()).orElse(null);
            String imgUrl = (principal != null) ? ("/uploads/" + principal.getUrl()) : null;

            int subtotal = ci.getPrecio() * ci.getCantidad();
            int disponible = p.getStock();

            return new CarritoItemDTO(
                    p.getId(),
                    p.getNombre(),
                    imgUrl,
                    ci.getPrecio(),
                    ci.getCantidad(),
                    subtotal,
                    disponible);
        }).toList();

        int total = items.stream().mapToInt(CarritoItemDTO::getSubtotal).sum();
        return new CarritoDTO(c.getId(), items, total);
    }

    public CarritoDTO agregar(Integer usuarioId, Integer productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0)
            cantidad = 1;

        var c = getOrCreate(usuarioId);

        int ok = productoRepo.reservarStock(productoId, cantidad);
        if (ok == 0) {
            Integer s = productoRepo.stockRestante(productoId);
            throw new IllegalStateException("No hay stock suficiente. Quedan " + (s == null ? 0 : s) + " unidades.");
        }

        var p = productoRepo.findById(productoId).orElseThrow();

        var existing = itemRepo.findByCarritoIdAndProductoId(c.getId(), p.getId());
        if (existing.isPresent()) {
            var it = existing.get();
            it.setCantidad(it.getCantidad() + cantidad);
            itemRepo.save(it);
        } else {
            var pk = new CarritoItemPK();
            pk.setCarritoId(c.getId());
            pk.setProductoId(p.getId());

            var it = new CarritoItem();
            it.setId(pk);
            it.setCarrito(c);
            it.setProducto(p);
            it.setCantidad(cantidad);
            it.setPrecio(p.getPrecio());
            itemRepo.save(it);
        }
        return mapDTO(c);
    }

    public CarritoDTO actualizarCantidad(Integer usuarioId, Integer productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0)
            throw new IllegalStateException("La cantidad mínima es 1");

        var c = getOrCreate(usuarioId);
        var it = itemRepo.findByCarritoIdAndProductoId(c.getId(), productoId)
                .orElseThrow(() -> new IllegalStateException("El producto no está en el carrito"));

        int delta = cantidad - it.getCantidad();
        if (delta > 0) {
            int ok = productoRepo.reservarStock(productoId, delta);
            if (ok == 0) {
                Integer s = productoRepo.stockRestante(productoId);
                throw new IllegalStateException(
                        "No hay stock suficiente. Quedan " + (s == null ? 0 : s) + " unidades.");
            }
        } else if (delta < 0) {
            productoRepo.liberarStock(productoId, -delta);
        }

        it.setCantidad(cantidad);
        itemRepo.save(it);
        return mapDTO(c);
    }

    public CarritoDTO eliminarItem(Integer usuarioId, Integer productoId) {
        var c = getOrCreate(usuarioId);
        itemRepo.findByCarritoIdAndProductoId(c.getId(), productoId).ifPresent(it -> {
            productoRepo.liberarStock(productoId, it.getCantidad());
            itemRepo.delete(it);
        });
        return mapDTO(c);
    }

    public void vaciar(Integer usuarioId) {
        var c = getOrCreate(usuarioId);
        var items = itemRepo.findByCarritoId(c.getId());
        for (var it : items) {
            productoRepo.liberarStock(it.getProducto().getId(), it.getCantidad());
        }
        itemRepo.deleteAll(items);
    }

    public Integer checkout(Integer usuarioId, CheckoutReq req) {
        var c = getOrCreate(usuarioId);
        var items = itemRepo.findByCarritoId(c.getId());
        if (items.isEmpty())
            throw new IllegalStateException("Carrito vacío");

        int totalBruto = items.stream().mapToInt(i -> i.getPrecio() * i.getCantidad()).sum();
        int comision = Math.round(totalBruto * 5 / 100.0f);
        int netoVendedores = totalBruto - comision;

        EstadoPedido estadoNuevo = estadoPedidoRepo.findByCodigo("EN_CURSO")
                .orElseThrow();

        var pedido = new Pedido();
        pedido.setCompradorId(usuarioId);
        pedido.setTotalBruto(totalBruto);
        pedido.setTotalComision(comision);
        pedido.setTotalNetoVendedores(netoVendedores);
        pedido.setEstadoPedido(estadoNuevo);
        pedido.setFechaCreacion(Instant.now());
        var entrega = LocalDate.now().plusDays(5);
        pedido.setFechaEstimadaEntrega(entrega);

        pedido = pedidoRepo.save(pedido);

        for (var ci : items) {
            var p = ci.getProducto();

            var pi = new PedidoItem();
            var pk = new PedidoItemPK();
            pk.setPedidoId(pedido.getId());
            pk.setProductoId(p.getId());
            pi.setId(pk);

            pi.setPedido(pedido);
            pi.setProducto(p);
            pi.setVendedorId(p.getVendedor().getId());
            pi.setCantidad(ci.getCantidad());
            pi.setPrecioUnitario(ci.getPrecio());

            int sub = ci.getCantidad() * ci.getPrecio();
            pi.setSubtotal(sub);

            int com = Math.round(sub * 5 / 100.0f);
            pi.setComision(com);
            pi.setNetoVendedor(sub - com);
            pi.setDebeEntregarseEl(entrega);

            pedidoItemRepo.save(pi);

        }

        Integer tarjetaId = null;
        if (Boolean.TRUE.equals(req.getGuardarTarjeta()) && req.getTokenPasarela() != null) {
            var tg = new TarjetaGuardada();
            tg.setUsuarioId(usuarioId);
            tg.setTokenPasarela(req.getTokenPasarela());
            tg.setUltimos4(req.getUltimos4());
            tg.setMarca(req.getMarca());
            tg.setExpiracionMes(req.getExpiracionMes().shortValue());
            tg.setExpiracionAnio(req.getExpiracionAnio().shortValue());
            tg.setTitular(req.getTitular());
            tarjetaId = tarjetaRepo.save(tg).getId();
        } else if (req.getTarjetaGuardadaId() != null) {
            tarjetaId = req.getTarjetaGuardadaId();
        }

        var pago = new Pago();
        pago.setPedidoId(pedido.getId());
        pago.setUsuarioId(usuarioId);
        pago.setMonto((long) totalBruto);
        pago.setMetodo("TARJETA");
        pago.setTarjetaGuardadaId(tarjetaId);
        pago.setEstado("APROBADO");
        pago.setReferenciaPasarela("SIM-" + UUID.randomUUID());
        pagoRepo.save(pago);

        itemRepo.deleteAll(items);
        c.setEstaVigente(false);
        carritoRepo.save(c);

        return pedido.getId();
    }

    public List<TarjetaGuardada> tarjetasDe(Integer usuarioId) {
        return tarjetaRepo.findByUsuarioId(usuarioId);
    }
}
