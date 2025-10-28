package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.comun.EmailService;
import com.ecommerce.gt.ecommerce_gt.producto.dto.ModProductoDTO;
import com.ecommerce.gt.ecommerce_gt.producto.dto.ProductoModeracionDTO;
import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * SERVICIO DE MODERACIÓN DE PRODUCTOS.
 * PERMITE LISTAR PENDIENTES, APROBAR, RECHAZAR Y CONSULTAR HISTORIAL.
 * TAMBIÉN ENVÍA CORREOS AL VENDEDOR SOBRE EL RESULTADO DE LA REVISIÓN.
 */
@Service
@RequiredArgsConstructor
public class ModeracionService {

    /** REPOSITORIO DE PRODUCTOS */
    private final ProductoRepository productoRepo;
    /** REPOSITORIO DE IMÁGENES DE PRODUCTO */
    private final ProductoImagenRepository imgRepo;
    /** REPOSITORIO DE ESTADOS DE MODERACIÓN */
    private final EstadoModeracionProductoRepository estadoRepo;
    /** SERVICIO DE CORREO PARA NOTIFICAR AL VENDEDOR */
    private final EmailService emailService;

    /**
     * DEVUELVE EL HISTORIAL DE PRODUCTOS MODERADOS (APROBADOS O RECHAZADOS).
     *
     * @param pageable PARÁMETROS DE PÁGINA
     * @return PÁGINA DE ModProductoDTO
     */
    public Page<ModProductoDTO> historial(Pageable pageable) {
        return productoRepo
                .findByEstadoModCodigoIn(List.of("APROBADO", "RECHAZADO"), pageable)
                .map(this::toModProductoDTO);
    }

    /**
     * CONVIERTE UNA ENTIDAD PRODUCTO A DTO DE HISTORIAL DE MODERACIÓN.
     *
     * @param p ENTIDAD PRODUCTO
     * @return DTO CON DATOS BÁSICOS, IMAGEN Y ESTADO DE MODERACIÓN
     */
    private ModProductoDTO toModProductoDTO(Producto p) {
        var img = imgRepo.findFirstByProductoIdOrderByIdDesc(p.getId()).orElse(null);
        String imageUrl = (img != null) ? ("/uploads/" + img.getUrl()) : null;

        return new ModProductoDTO(
                p.getId(),
                p.getNombre(),
                p.getCategoria().getNombre(),
                p.getPrecio().longValue(),
                p.getStock(),
                p.getVendedor().getNombre(),
                imageUrl,
                p.getCreadoEn() != null ? p.getCreadoEn().toString() : null,
                p.getEstadoMod().getCodigo(),
                p.getComentarioRechazo());
    }

    /**
     * LISTA LOS PRODUCTOS EN ESTADO "PENDIENTE" PARA SER MODERADOS.
     *
     * @param pagina NÚMERO DE PÁGINA (0-BASED)
     * @param size   TAMAÑO DE PÁGINA
     * @return PÁGINA DE ProductoModeracionDTO ORDENADA POR FECHA DE CREACIÓN ASC
     */
    public Page<ProductoModeracionDTO> listarPendientes(int pagina, int size) {
        var p = PageRequest.of(pagina, size);
        return productoRepo
                .findByEstadoModCodigoOrderByCreadoEnAsc("PENDIENTE", p)
                .map(this::toDTO);
    }

    /**
     * CONVIERTE UNA ENTIDAD PRODUCTO A DTO PARA LA BANDEJA DE MODERACIÓN.
     *
     * @param pr ENTIDAD PRODUCTO
     * @return DTO CON DATOS CLAVE PARA REVISIÓN
     */
    private ProductoModeracionDTO toDTO(Producto pr) {
        var img = imgRepo.findFirstByProductoIdOrderByIdDesc(pr.getId()).orElse(null);
        String url = (img != null) ? "/uploads/" + img.getUrl() : null;
        return new ProductoModeracionDTO(
                pr.getId(),
                pr.getNombre(),
                pr.getCategoria().getNombre(),
                pr.getPrecio(),
                pr.getStock(),
                pr.getVendedor().getNombre(),
                url,
                pr.getCreadoEn().toString());
    }

    /**
     * APRUEBA UN PRODUCTO Y NOTIFICA AL VENDEDOR POR CORREO.
     *
     * @param id          ID DEL PRODUCTO
     * @param moderadorId ID DEL MODERADOR QUE APRUEBA
     */
    public void aprobar(Integer id, Integer moderadorId) {
        var prod = productoRepo.findById(id).orElseThrow();
        var aprobado = estadoRepo.findByCodigo("APROBADO").orElseThrow();

        prod.setEstadoMod(aprobado);
        prod.setUltimaRevisionEn(Instant.now());
        prod.setUltimoModeradorId(moderadorId);
        prod.setComentarioRechazo(null);
        productoRepo.save(prod);

        var para = prod.getVendedor().getCorreo();
        var asunto = "Tu producto fue APROBADO";
        var cuerpo = "Hola " + prod.getVendedor().getNombre() + ",\n\n" +
                "Tu producto \"" + prod.getNombre() + "\" ha sido APROBADO y ya es visible en el catálogo.\n" +
                "¡Gracias por publicar con nosotros!\n" +
                "Moderación de Ecommerce GT Team\n";
        emailService.enviar(para, asunto, cuerpo);
    }

    /**
     * RECHAZA UN PRODUCTO INDICANDO EL MOTIVO Y NOTIFICA AL VENDEDOR.
     *
     * @param id          ID DEL PRODUCTO
     * @param moderadorId ID DEL MODERADOR QUE RECHAZA
     * @param motivo      TEXTO DEL MOTIVO DE RECHAZO
     */
    public void rechazar(Integer id, Integer moderadorId, String motivo) {
        var prod = productoRepo.findById(id).orElseThrow();
        var rechazado = estadoRepo.findByCodigo("RECHAZADO").orElseThrow();

        prod.setEstadoMod(rechazado);
        prod.setUltimaRevisionEn(Instant.now());
        prod.setUltimoModeradorId(moderadorId);
        prod.setComentarioRechazo(motivo);
        productoRepo.save(prod);

        var para = prod.getVendedor().getCorreo();
        var asunto = "Tu producto fue RECHAZADO";
        var cuerpo = "Hola " + prod.getVendedor().getNombre() + ",\n\n" +
                "Tu producto \"" + prod.getNombre() + "\" fue RECHAZADO.\n" +
                "Motivo: " + (motivo == null ? "(sin detalle)" : motivo) + "\n\n" +
                "Puedes editarlo y volver a enviarlo para revisión.\n" +
                "Moderación de Ecommerce GT Team\n";
        emailService.enviar(para, asunto, cuerpo);
    }

    /**
     * LISTA EL HISTORIAL DE MODERACIONES REALIZADAS POR UN MODERADOR.
     *
     * @param moderadorId ID DEL MODERADOR
     * @param pageable    PARÁMETROS DE PÁGINA
     * @return PÁGINA DE ModProductoDTO APROBADOS O RECHAZADOS
     */
    public Page<ModProductoDTO> historialDe(Integer moderadorId, Pageable pageable) {
        return productoRepo
                .findByEstadoModCodigoInAndUltimoModeradorId(
                        List.of("APROBADO", "RECHAZADO"), moderadorId, pageable)
                .map(this::toModProductoDTO);
    }
}
