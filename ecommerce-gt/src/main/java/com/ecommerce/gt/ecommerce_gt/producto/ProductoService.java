package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.files.FilesStorageService;
import com.ecommerce.gt.ecommerce_gt.producto.dto.*;
import com.ecommerce.gt.ecommerce_gt.producto.entity.EstadoArticulo;
import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;
import com.ecommerce.gt.ecommerce_gt.producto.entity.ProductoImagen;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepo;
    private final CategoriaRepository categoriaRepo;
    private final EstadoModeracionProductoRepository estadoRepo;
    private final ProductoImagenRepository imgRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProductoReviewRepository reviewRepo;

    private final FilesStorageService storage;

    public Page<ProductoResponse> listarPublico(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("id").descending());
        return productoRepo.findByEstadoModCodigo("APROBADO", p).map(this::toResponse);
    }

    public Page<ProductoResponse> listarMisProductos(Integer vendedorId, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("id").descending());
        return productoRepo.findByVendedorId(vendedorId, p).map(this::toResponse);
    }

    public ProductoResponse crear(Integer vendedorId, ProductoCrearRequest req, MultipartFile imagen) {
        var vendedor = usuarioRepo.getReferenceById(vendedorId);
        var categoria = categoriaRepo.findById(req.getCategoriaId()).orElseThrow();
        var estadoPend = estadoRepo.findByCodigo("PENDIENTE")
                .orElseThrow(() -> new IllegalStateException("Falta catálogo PENDIENTE"));

        var entity = new Producto();
        entity.setVendedor(vendedor);
        entity.setNombre(req.getNombre());
        entity.setDescripcion(req.getDescripcion());
        entity.setPrecio(req.getPrecioCents().intValue());
        entity.setStock(req.getStock());
        entity.setEstadoArticulo(EstadoArticulo.valueOf(req.getEstadoArticulo()));
        entity.setCategoria(categoria);
        entity.setEstadoMod(estadoPend);
        entity.setUltimaRevisionEn(null);
        entity.setCreadoEn(Instant.now());
        entity.setActualizadoEn(Instant.now());

        entity = productoRepo.saveAndFlush(entity);

        if (imagen != null && !imagen.isEmpty()) {
            var filename = storage.save(imagen);
            var img = new ProductoImagen();
            img.setProducto(entity);
            img.setUrl(filename);
            img.setCreadoEn(Instant.now());
            imgRepo.saveAndFlush(img);
        }
        return toResponse(entity);
    }

    public ProductoResponse actualizar(Integer id, Integer vendedorId, ProductoActualizarRequest req,
            MultipartFile nuevaImagen) {
        var entity = productoRepo.findById(id).orElseThrow();
        if (!entity.getVendedor().getId().equals(vendedorId)) {
            throw new IllegalArgumentException("No puedes modificar productos de otro vendedor");
        }

        entity.setNombre(req.getNombre());
        entity.setDescripcion(req.getDescripcion());
        entity.setPrecio(req.getPrecioCents().intValue());
        entity.setStock(req.getStock());
        entity.setEstadoArticulo(EstadoArticulo.valueOf(req.getEstadoArticulo()));
        entity.setCategoria(categoriaRepo.findById(req.getCategoriaId()).orElseThrow());

        var estadoPend = estadoRepo.findByCodigo("PENDIENTE").orElseThrow();
        entity.setEstadoMod(estadoPend);
        entity.setUltimaRevisionEn(Instant.now());
        entity.setActualizadoEn(Instant.now());

        entity = productoRepo.save(entity);

        if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
            var filename = storage.save(nuevaImagen);
            var img = new ProductoImagen();
            img.setProducto(entity);
            img.setUrl(filename);
            img.setCreadoEn(Instant.now());
            imgRepo.saveAndFlush(img);
        }

        return toResponse(entity);
    }

    private ProductoResponse toResponse(Producto p) {
        var img = imgRepo.findFirstByProductoIdOrderByIdDesc(p.getId()).orElse(null);
        Double avg = reviewRepo.avgByProducto(p.getId());
        int cnt = reviewRepo.countByProductoId(p.getId());

        String imageUrl = null;
        if (img != null) {

            imageUrl = "/uploads/" + img.getUrl();
        }

        return new ProductoResponse(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio().longValue(),
                p.getStock(),
                p.getEstadoArticulo().name(),
                p.getCategoria().getNombre(),
                p.getEstadoMod().getCodigo(),
                imageUrl,
                p.getVendedor().getId(),
                avg == null ? 0.0 : avg,
                cnt,
                p.getCategoria().getId());

    }

    public ProductoResponse obtenerMio(Integer vendedorId, Integer id) {
        var p = productoRepo.findById(id).orElseThrow();
        if (!p.getVendedor().getId().equals(vendedorId)) {
            throw new IllegalArgumentException("No puedes ver productos de otro vendedor");
        }
        return toResponse(p);
    }
}
