package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByCompradorIdOrderByFechaCreacionDesc(Integer compradorId);

    @Query("""
              select p from Pedido p
              where p.estadoPedido.codigo = :codigo
              order by p.fechaCreacion desc
            """)
    Page<Pedido> findByEstadoCodigo(@Param("codigo") String codigo, Pageable pageable);
}