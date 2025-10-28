package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.Carrito;

/**
 * REPOSITORIO JPA PARA LA ENTIDAD CARRITO.
 * PERMITE CONSULTAR Y GESTIONAR LOS CARRITOS DE COMPRA DE LOS USUARIOS.
 */
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    /**
     * BUSCA EL CARRITO MÁS RECIENTE Y ACTIVO DE UN USUARIO.
     * 
     * @param usuarioId ID DEL USUARIO.
     * @return CARRITO ACTIVO MÁS RECIENTE, SI EXISTE
     */
    Optional<Carrito> findFirstByUsuarioIdAndEstaVigenteTrueOrderByIdDesc(Integer usuarioId);
}
