package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.TarjetaGuardada;

/**
 * REPOSITORIO JPA PARA LA ENTIDAD TARJETA_GUARDADA.
 * PERMITE CONSULTAR Y ADMINISTRAR LAS TARJETAS ASOCIADAS A UN USUARIO.
 */
@Repository
public interface TarjetaGuardadaRepository extends JpaRepository<TarjetaGuardada, Integer> {

    /**
     * BUSCA TODAS LAS TARJETAS GUARDADAS DE UN USUARIO.
     *
     * @param usuarioId ID DEL USUARIO PROPIETARIO DE LAS TARJETAS.
     * @return LISTA DE TARJETAS GUARDADAS DEL USUARIO.
     */
    List<TarjetaGuardada> findByUsuarioId(Integer usuarioId);
}
