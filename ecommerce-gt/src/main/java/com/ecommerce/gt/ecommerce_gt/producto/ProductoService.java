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

/**
 * SERVICIO DE PRODUCTOS.
 * LISTA PRODUCTOS PÚBLICOS, LISTA PRODUCTOS DEL VENDEDOR,
 * CREA, ACTUALIZA Y CONSTRUYE RESPUESTAS (DTO) INCLUYENDO IMAGEN Y RATING.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    /** REPOSITORIO DE PRODUCTOS */
    private final ProductoRepository productoRepo;
    /** REPOSITORIO DE CATEGORÍAS */
    private final CategoriaRepository categoriaRepo;
    /** REPOSITORIO DE ESTADOS DE MODERACIÓN */
    private final EstadoModeracionProductoRepository estadoRepo;
    /** REPOSITORIO DE IMÁGENES */
    private final ProductoImagenRepository imgRepo;
    /** REPOSITORIO DE USUARIOS */
    private final UsuarioRepository usuarioRepo;
    /** REPOSITORIO DE RESEÑAS */
    private final ProductoReviewRepository reviewRepo;
    /** SERVICIO DE ARCHIVOS PARA GUARDAR IMÁGENES */
    private final FilesStorageService storage;

    /**
     * LISTA PRODUCTOS APROBADOS PARA EL CATÁLOGO PÚBLICO.
     *
     * @param page NÚMERO DE PÁGINA
     * @param size TAMAÑO DE PÁGINA
     * @return PÁGINA DE ProductoResponse
     */
    public Page<ProductoResponse> listarPublico(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("id").descending());
        return productoRepo.findByEstadoModCodigo("APROBADO", p).map(this::toResponse);
    }

    /**
     * LISTA LOS PRODUCTOS DEL VENDEDOR AUTENTICADO.
     *
     * @param vendedorId ID DEL VENDEDOR
     * @param page       NÚMERO DE PÁGINA
     * @param size       TAMAÑO DE PÁGINA
     * @return PÁGINA DE ProductoResponse
     */
    public Page<ProductoResponse> listarMisProductos(Integer vendedorId, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("id").descending());
        return productoRepo.findByVendedorId(vendedorId, p).map(this::toResponse);
    }

    /**
     * CREA UN PRODUCTO NUEVO PARA EL VENDEDOR.
     * SI SE ENVÍA IMAGEN, LA GUARDA Y LA ASOCIA.
     * EL PRODUCTO QUEDA EN ESTADO DE MODERACIÓN "PENDIENTE".
     *
     * @param vendedorId ID DEL VENDEDOR
     * @param req        DATOS DEL PRODUCTO
     * @param imagen     IMAGEN OPCIONAL
     * @return ProductoResponse CON DATOS DEL NUEVO PRODUCTO
     */
    public ProductoResponse crear(Integer vendedorId, ProductoCrearRequest req, MultipartFile imagen) {
        var vendedor = usuarioRepo.getReferenceById(vendedorId);
        var categoria = categoriaRepo.findById(req.getCategoriaId()).orElseThrow();
        var estadoPend = estadoRepo.findByCodigo("PENDIENTE")
                .orElseThrow(() -> new IllegalStateException("FALTA CATÁLOGO PENDIENTE"));

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

    /**
     * ACTUALIZA UN PRODUCTO EXISTENTE DEL VENDEDOR.
     * RESETEA MODERACIÓN A "PENDIENTE" Y PUEDE CAMBIAR IMAGEN.
     *
     * @param id          ID DEL PRODUCTO
     * @param vendedorId  ID DEL VENDEDOR (SE VALIDA PROPIEDAD)
     * @param req         DATOS A ACTUALIZAR
     * @param nuevaImagen NUEVA IMAGEN OPCIONAL
     * @return ProductoResponse ACTUALIZADO
     */
    public ProductoResponse actualizar(Integer id, Integer vendedorId, ProductoActualizarRequest req,
            MultipartFile nuevaImagen) {
        var entity = productoRepo.findById(id).orElseThrow();
        if (!entity.getVendedor().getId().equals(vendedorId)) {
            throw new IllegalArgumentException("NO PUEDES MODIFICAR PRODUCTOS DE OTRO VENDEDOR");
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

    /**
     * CONSTRUYE EL DTO DE RESPUESTA PARA UN PRODUCTO.
     * INCLUYE URL DE IMAGEN, PROMEDIO Y CANTIDAD DE RESEÑAS.
     *
     * @param p ENTIDAD PRODUCTO
     * @return ProductoResponse CON CAMPOS LISTOS PARA EL FRONTEND
     */
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

    /**
     * OBTIENE UN PRODUCTO DEL VENDEDOR POR ID.
     * VALIDA QUE EL PRODUCTO SEA DEL PROPIO VENDEDOR.
     *
     * @param vendedorId ID DEL VENDEDOR
     * @param id         ID DEL PRODUCTO
     * @return ProductoResponse DEL PRODUCTO PROPIO
     */
    public ProductoResponse obtenerMio(Integer vendedorId, Integer id) {
        var p = productoRepo.findById(id).orElseThrow();
        if (!p.getVendedor().getId().equals(vendedorId)) {
            throw new IllegalArgumentException("NO PUEDES VER PRODUCTOS DE OTRO VENDEDOR");
        }
        return toResponse(p);
    }
}
