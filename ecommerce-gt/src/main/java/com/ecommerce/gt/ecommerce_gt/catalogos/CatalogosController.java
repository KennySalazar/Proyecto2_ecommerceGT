package com.ecommerce.gt.ecommerce_gt.catalogos;

import com.ecommerce.gt.ecommerce_gt.producto.CategoriaRepository;
import com.ecommerce.gt.ecommerce_gt.producto.entity.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CatalogosController {

    private final CategoriaRepository categoriaRepo;

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
