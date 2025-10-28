package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.EstadoPedido;

/**
 * REPOSITORIO JPA PARA LA ENTIDAD ESTADO_PEDIDO.
 * SE UTILIZA PARA CONSULTAR Y ADMINISTRAR LOS ESTADOS
 * DE LOS PEDIDOS DENTRO DEL SISTEMA.
 */
@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Integer> {

    /**
     * BUSCA UN ESTADO DE PEDIDO SEGÚN SU CÓDIGO ÚNICO.
     * 
     * @param codigo CÓDIGO DEL ESTADO (EJEMPLO: "EN_CURSO", "ENTREGADO", ETC.).
     * @return ESTADO DE PEDIDO SI EXISTE, EN UN Optional.
     */
    Optional<EstadoPedido> findByCodigo(String codigo);
}
