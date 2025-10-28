package com.ecommerce.gt.ecommerce_gt.catalogos;

import com.ecommerce.gt.ecommerce_gt.producto.CategoriaRepository;
import com.ecommerce.gt.ecommerce_gt.producto.entity.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CONTROLADOR DE CATÁLOGOS.
 * EXPONE ENDPOINTS PÚBLICOS PARA CONSULTAR DATOS DE REFERENCIA,
 * COMO LAS CATEGORÍAS DE PRODUCTOS.
 */
@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CatalogosController {

    /** REPOSITORIO DE CATEGORÍAS DE PRODUCTOS */
    private final CategoriaRepository categoriaRepo;

    /**
     * DEVUELVE TODAS LAS CATEGORÍAS DISPONIBLES EN FORMATO SIMPLE.
     * CADA CATEGORÍA SE REPRESENTA CON SU ID Y NOMBRE.
     *
     * @return LISTA DE CATEGORÍAS COMO MAPAS
     */
    @GetMapping("/categorias")
    public List<Map<String, Object>> categorias() {
        List<Categoria> categorias = categoriaRepo.findAll();

        return categorias.stream()
                .map(c -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", c.getId());
                    m.put("nombre", c.getNombre());
                    return m;
                })
                .collect(Collectors.toList());
    }
}
