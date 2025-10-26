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

@Service
@RequiredArgsConstructor
public class ModeracionService {
    private final ProductoRepository productoRepo;
    private final ProductoImagenRepository imgRepo;
    private final EstadoModeracionProductoRepository estadoRepo;
    private final EmailService emailService;

    public Page<ModProductoDTO> historial(Pageable pageable) {
        return productoRepo
                .findByEstadoModCodigoIn(List.of("APROBADO", "RECHAZADO"), pageable)
                .map(this::toModProductoDTO);
    }

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

    public Page<ProductoModeracionDTO> listarPendientes(int pagina, int size) {
        var p = PageRequest.of(pagina, size);
        return productoRepo
                .findByEstadoModCodigoOrderByCreadoEnAsc("PENDIENTE", p)
                .map(this::toDTO);
    }

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

    public void aprobar(Integer id, Integer moderadorId) {
        var prod = productoRepo.findById(id).orElseThrow();
        var aprobado = estadoRepo.findByCodigo("APROBADO").orElseThrow();
        prod.setEstadoMod(aprobado);
        prod.setUltimaRevisionEn(Instant.now());
        prod.setUltimoModeradorId(moderadorId); // ✅ quién moderó
        prod.setComentarioRechazo(null); // limpia si venía de rechazo
        productoRepo.save(prod);

        // ====== Enviar correo ======
        var para = prod.getVendedor().getCorreo(); // asumiendo getter
        var asunto = "Tu producto fue APROBADO";
        var cuerpo = "Hola " + prod.getVendedor().getNombre() + ",\n\n" +
                "Tu producto \"" + prod.getNombre() + "\" ha sido APROBADO y ya es visible en el catálogo.\n" +
                "¡Gracias por publicar con nosotros!\n" + "Moderación de Ecommerce GT Team\n";
        emailService.enviar(para, asunto, cuerpo);
    }

    public void rechazar(Integer id, Integer moderadorId, String motivo) {
        var prod = productoRepo.findById(id).orElseThrow();
        var rechazado = estadoRepo.findByCodigo("RECHAZADO").orElseThrow();
        prod.setEstadoMod(rechazado);
        prod.setUltimaRevisionEn(Instant.now());
        prod.setUltimoModeradorId(moderadorId);
        prod.setComentarioRechazo(motivo);
        productoRepo.save(prod);

        // ====== Enviar correo ======
        var para = prod.getVendedor().getCorreo();
        var asunto = "Tu producto fue RECHAZADO";
        var cuerpo = "Hola " + prod.getVendedor().getNombre() + ",\n\n" +
                "Tu producto \"" + prod.getNombre() + "\" fue RECHAZADO.\n" +
                "Motivo: " + (motivo == null ? "(sin detalle)" : motivo) + "\n\n" +
                "Puedes editarlo y volver a enviarlo para revisión.\n" +
                "Moderación de Ecommerce GT Team\n";
        ;
        emailService.enviar(para, asunto, cuerpo);
    }

    public Page<ModProductoDTO> historialDe(Integer moderadorId, Pageable pageable) {
        return productoRepo
                .findByEstadoModCodigoInAndUltimoModeradorId(
                        java.util.List.of("APROBADO", "RECHAZADO"), moderadorId, pageable)
                .map(this::toModProductoDTO);
    }

}