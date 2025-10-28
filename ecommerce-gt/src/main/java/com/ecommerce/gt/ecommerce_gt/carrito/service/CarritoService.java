package com.ecommerce.gt.ecommerce_gt.carrito.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
import com.ecommerce.gt.ecommerce_gt.comun.EmailService;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoRepository;
import com.ecommerce.gt.ecommerce_gt.producto.ProductoImagenRepository;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * SERVICIO DEL CARRITO DE COMPRAS.
 * GESTIONA: VER CARRITO, AGREGAR/ACTUALIZAR/ELIMINAR ITEMS, VACIAR,
 * REALIZAR CHECKOUT, GUARDAR TARJETAS Y ENVIAR CORREO DE CONFIRMACIÓN.
 * LA CLASE ES TRANSACCIONAL PARA ASEGURAR CONSISTENCIA EN OPERACIONES.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CarritoService {

    /** REPOSITORIO DE CARRITOS */
    private final CarritoRepository carritoRepo;
    /** REPOSITORIO DE ITEMS DEL CARRITO */
    private final CarritoItemRepository itemRepo;
    /** REPOSITORIO DE PRODUCTOS (STOCK, PRECIOS) */
    private final ProductoRepository productoRepo;
    /** REPOSITORIO DE IMÁGENES DE PRODUCTO */
    private final ProductoImagenRepository imgRepo;
    /** REPOSITORIO DE TARJETAS GUARDADAS */
    private final TarjetaGuardadaRepository tarjetaRepo;
    /** REPOSITORIO DE PEDIDOS */
    private final PedidoRepository pedidoRepo;
    /** REPOSITORIO DE ITEMS DE PEDIDO */
    private final PedidoItemRepository pedidoItemRepo;
    /** REPOSITORIO DE ESTADOS DE PEDIDO */
    private final EstadoPedidoRepository estadoPedidoRepo;
    /** UTILIDAD JWT (NO SE USA DIRECTO AQUÍ, PERO ESTÁ INYECTADA) */
    private final JwtUtil jwt;
    /** REPOSITORIO DE PAGOS */
    private final PagoRepository pagoRepo;
    /** SERVICIO DE EMAIL PARA NOTIFICACIONES */
    private final EmailService email;
    /** REPOSITORIO DE USUARIOS (PARA CORREO) */
    private final UsuarioRepository usuarioRepo;

    /**
     * OBTIENE EL CARRITO ACTIVO DEL USUARIO O LO CREA SI NO EXISTE.
     *
     * @param usuarioId ID DEL USUARIO
     * @return CARRITO ACTIVO
     */
    private Carrito getOrCreate(Integer usuarioId) {
        return carritoRepo.findFirstByUsuarioIdAndEstaVigenteTrueOrderByIdDesc(usuarioId)
                .orElseGet(() -> {
                    var c = new Carrito();
                    c.setUsuario(new Usuario(usuarioId));
                    return carritoRepo.save(c);
                });
    }

    /**
     * MAPE A DTO DEL CARRITO CON SUS ITEMS Y TOTAL.
     * INCLUYE URL DE IMAGEN PRINCIPAL Y STOCK RESTANTE.
     *
     * @param c ENTIDAD CARRITO
     * @return CarritoDTO CON ITEMS Y TOTAL
     */
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

    /**
     * DEVUELVE EL CARRITO ACTUAL DEL USUARIO CON DETALLES Y TOTALES.
     *
     * @param usuarioId ID DEL USUARIO
     * @return CarritoDTO
     */
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

    /**
     * AGREGA UN PRODUCTO AL CARRITO, RESERVANDO STOCK.
     * SI NO HAY STOCK SUFICIENTE, LANZA EXCEPCIÓN.
     *
     * @param usuarioId  ID DEL USUARIO
     * @param productoId ID DEL PRODUCTO
     * @param cantidad   CANTIDAD A AGREGAR (DEFAULT 1)
     * @return CarritoDTO ACTUALIZADO
     */
    public CarritoDTO agregar(Integer usuarioId, Integer productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0)
            cantidad = 1;

        var c = getOrCreate(usuarioId);

        // RESERVA STOCK; SI NO RESERVA, REPORTA STOCK RESTANTE
        int ok = productoRepo.reservarStock(productoId, cantidad);
        if (ok == 0) {
            Integer s = productoRepo.stockRestante(productoId);
            throw new IllegalStateException("No hay stock suficiente. Quedan " + (s == null ? 0 : s) + " unidades.");
        }

        var p = productoRepo.findById(productoId).orElseThrow();

        // SI YA EXISTE EN EL CARRITO, SOLO SUMA LA CANTIDAD
        var existing = itemRepo.findByCarritoIdAndProductoId(c.getId(), p.getId());
        if (existing.isPresent()) {
            var it = existing.get();
            it.setCantidad(it.getCantidad() + cantidad);
            itemRepo.save(it);
        } else {
            // SI NO EXISTE, CREA NUEVO ITEM
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

    /**
     * ACTUALIZA LA CANTIDAD DE UN PRODUCTO EN EL CARRITO.
     * AJUSTA LAS RESERVAS DE STOCK SEGÚN EL CAMBIO.
     *
     * @param usuarioId  ID DEL USUARIO
     * @param productoId ID DEL PRODUCTO
     * @param cantidad   NUEVA CANTIDAD (>=1)
     * @return CarritoDTO ACTUALIZADO
     */
    public CarritoDTO actualizarCantidad(Integer usuarioId, Integer productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0)
            throw new IllegalStateException("La cantidad mínima es 1");

        var c = getOrCreate(usuarioId);
        var it = itemRepo.findByCarritoIdAndProductoId(c.getId(), productoId)
                .orElseThrow(() -> new IllegalStateException("El producto no está en el carrito"));

        int delta = cantidad - it.getCantidad();
        if (delta > 0) {
            // SE NECESITA RESERVAR STOCK EXTRA
            int ok = productoRepo.reservarStock(productoId, delta);
            if (ok == 0) {
                Integer s = productoRepo.stockRestante(productoId);
                throw new IllegalStateException(
                        "No hay stock suficiente. Quedan " + (s == null ? 0 : s) + " unidades.");
            }
        } else if (delta < 0) {
            // SE LIBERA STOCK SI LA CANTIDAD DISMINUYE
            productoRepo.liberarStock(productoId, -delta);
        }

        it.setCantidad(cantidad);
        itemRepo.save(it);
        return mapDTO(c);
    }

    /**
     * ELIMINA UN ITEM DEL CARRITO Y LIBERA SU STOCK.
     *
     * @param usuarioId  ID DEL USUARIO
     * @param productoId ID DEL PRODUCTO A ELIMINAR
     * @return CarritoDTO ACTUALIZADO
     */
    public CarritoDTO eliminarItem(Integer usuarioId, Integer productoId) {
        var c = getOrCreate(usuarioId);
        itemRepo.findByCarritoIdAndProductoId(c.getId(), productoId).ifPresent(it -> {
            productoRepo.liberarStock(productoId, it.getCantidad());
            itemRepo.delete(it);
        });
        return mapDTO(c);
    }

    /**
     * VACÍA EL CARRITO COMPLETO, LIBERANDO STOCK DE TODOS LOS ITEMS.
     *
     * @param usuarioId ID DEL USUARIO
     */
    public void vaciar(Integer usuarioId) {
        var c = getOrCreate(usuarioId);
        var items = itemRepo.findByCarritoId(c.getId());
        for (var it : items) {
            productoRepo.liberarStock(it.getProducto().getId(), it.getCantidad());
        }
        itemRepo.deleteAll(items);
    }

    /**
     * REALIZA EL CHECKOUT:
     * 1) VALIDA QUE EXISTA CARRITO CON ITEMS.
     * 2) CALCULA TOTALES (COMISIÓN 5%).
     * 3) CREA PEDIDO E ITEMS DE PEDIDO.
     * 4) REGISTRA PAGO
     * 5) VACÍA CARRITO
     * 6) ENVÍA CORREO DE CONFIRMACIÓN AL COMPRADOR.
     *
     * @param usuarioId ID DEL USUARIO
     * @param req       DATOS DE PAGO/TARJETA
     * @return ID DEL PEDIDO CREADO
     */
    public Integer checkout(Integer usuarioId, CheckoutReq req) {
        var c = getOrCreate(usuarioId);
        var items = itemRepo.findByCarritoId(c.getId());
        if (items.isEmpty())
            throw new IllegalStateException("Carrito vacío");

        // CALCULA TOTALES
        int totalBruto = items.stream().mapToInt(i -> i.getPrecio() * i.getCantidad()).sum();
        int comision = Math.round(totalBruto * 5 / 100.0f);
        int netoVendedores = totalBruto - comision;

        // ESTADO INICIAL DEL PEDIDO
        EstadoPedido estadoNuevo = estadoPedidoRepo.findByCodigo("EN_CURSO")
                .orElseThrow();

        // CREA PEDIDO
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

        // CREA ITEMS DE PEDIDO Y CALCULA COMISIONES POR ITEM
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

        // GESTIÓN DE TARJETA GUARDADA
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

        // LIMPIA CARRITO
        itemRepo.deleteAll(items);
        c.setEstaVigente(false);
        carritoRepo.save(c);

        // ENVÍA CORREO DE CONFIRMACIÓN
        enviarCorreoConfirmacion(pedido.getId(), usuarioId);

        return pedido.getId();
    }

    /**
     * LISTA TARJETAS GUARDADAS DEL USUARIO.
     *
     * @param usuarioId ID DEL USUARIO
     * @return LISTA DE TARJETAS
     */
    public List<TarjetaGuardada> tarjetasDe(Integer usuarioId) {
        return tarjetaRepo.findByUsuarioId(usuarioId);
    }

    /**
     * ENVÍA CORREO DE CONFIRMACIÓN AL COMPRADOR CON EL DETALLE DEL PEDIDO.
     *
     * @param pedidoId  ID DEL PEDIDO
     * @param usuarioId ID DEL USUARIO
     */
    private void enviarCorreoConfirmacion(Integer pedidoId, Integer usuarioId) {
        var usuario = usuarioRepo.findById(usuarioId).orElse(null);
        if (usuario == null)
            return;

        var pedido = pedidoRepo.findById(pedidoId).orElseThrow();
        List<PedidoItem> items = pedidoItemRepo.findByPedidoId(pedidoId);
        String asunto = "Pedido #" + pedidoId + " confirmado";

        var fmtQ = java.text.NumberFormat.getCurrencyInstance(new Locale("es", "GT"));
        var fmtFecha = DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("es", "GT"));

        StringBuilder filas = new StringBuilder();
        for (var it : items) {
            var nombre = it.getProducto().getNombre();
            String subtotal = fmtQ.format(it.getSubtotal());
            String pu = fmtQ.format(it.getPrecioUnitario());
            filas.append("""
                        <tr>
                          <td style="padding:6px 8px">%s</td>
                          <td style="padding:6px 8px;text-align:center">%d</td>
                          <td style="padding:6px 8px;text-align:right">%s</td>
                          <td style="padding:6px 8px;text-align:right">%s</td>
                        </tr>
                    """.formatted(nombre, it.getCantidad(), pu, subtotal));
        }

        String total = fmtQ.format(pedido.getTotalBruto());
        String comision = fmtQ.format(pedido.getTotalComision());
        String neto = fmtQ.format(pedido.getTotalNetoVendedores());
        String entrega = pedido.getFechaEstimadaEntrega() != null
                ? pedido.getFechaEstimadaEntrega().format(fmtFecha)
                : "-";

        // HTML DEL CORREO
        String html = """
                    <div style="font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;color:#111">
                      <h2 style="margin:0 0 12px">¡Gracias por tu compra, %s!</h2>
                      <p style="margin:0 0 6px">Tu pedido <b>#%d</b> fue recibido y está <b>en curso</b>.</p>
                      <p style="margin:0 0 12px">Entrega estimada: <b>%s</b></p>

                      <table width="100%%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;border:1px solid #e5e7eb">
                        <thead>
                         <h4 style="margin:0 0 25px">Detalle:</h4>
                          <tr style="background:#f3f4f6">
                            <th align="left" style="padding:8px;border-bottom:1px solid #e5e7eb">Producto</th>
                            <th align="center" style="padding:8px;border-bottom:1px solid #e5e7eb">Cant.</th>
                            <th align="right" style="padding:8px;border-bottom:1px solid #e5e7eb">P. unitario</th>
                            <th align="right" style="padding:8px;border-bottom:1px solid #e5e7eb">Subtotal</th>
                          </tr>
                        </thead>
                        <tbody>
                          %s
                        </tbody>
                      </table>
                    </div>
                """
                .formatted(
                        usuario.getNombre(),
                        pedidoId,
                        entrega,
                        filas.toString(),
                        total, comision, neto,
                        "http://localhost:4200/mis-compras");

        email.enviarHtml(usuario.getCorreo(), asunto, html);
    }
}
