package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * REPOSITORIO JPA DE PRODUCTOS.
 * PERMITE LISTAR, PAGINAR Y OPERAR SOBRE STOCK.
 * TAMBIÉN FILTRA POR ESTADO DE MODERACIÓN Y POR MODERADOR.
 */
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * LISTA LOS PRODUCTOS DE UN VENDEDOR CON PAGINACIÓN.
     *
     * @param vendedorId ID DEL VENDEDOR
     * @param pageable   PARÁMETROS DE PÁGINA
     * @return PÁGINA DE PRODUCTOS
     */
    Page<Producto> findByVendedorId(Integer vendedorId, Pageable pageable);

    /**
     * LISTA PRODUCTOS POR CÓDIGO DE ESTADO DE MODERACIÓN.
     *
     * @param codigo   CÓDIGO (PENDIENTE, APROBADO, RECHAZADO)
     * @param pageable PARÁMETROS DE PÁGINA
     * @return PÁGINA DE PRODUCTOS
     */
    Page<Producto> findByEstadoModCodigo(String codigo, Pageable pageable);

    /**
     * RESERVA STOCK RESTANDO CANTIDAD SI HAY DISPONIBLE.
     * DEVUELVE 1 SI ACTUALIZÓ, 0 SI NO HABÍA STOCK SUFICIENTE.
     *
     * @param id   ID DEL PRODUCTO
     * @param cant CANTIDAD A RESERVAR
     * @return 1 SI OK, 0 SI FALLA
     */
    @Modifying
    @Query("update Producto p set p.stock = p.stock - :cant where p.id = :id and p.stock >= :cant")
    int reservarStock(@Param("id") Integer id, @Param("cant") int cant);

    /**
     * LIBERA STOCK SUMANDO CANTIDAD.
     *
     * @param id   ID DEL PRODUCTO
     * @param cant CANTIDAD A LIBERAR
     * @return FILAS AFECTADAS
     */
    @Modifying
    @Query("update Producto p set p.stock = p.stock + :cant where p.id = :id")
    int liberarStock(@Param("id") Integer id, @Param("cant") int cant);

    /**
     * OBTIENE EL STOCK ACTUAL DE UN PRODUCTO.
     *
     * @param id ID DEL PRODUCTO
     * @return STOCK (PUEDE SER NULL SI NO USA STOCK)
     */
    @Query("select p.stock from Producto p where p.id = :id")
    Integer stockRestante(@Param("id") Integer id);

    /**
     * LISTA PRODUCTOS POR ESTADO, ORDENADOS POR FECHA DE CREACIÓN ASC.
     *
     * @param codigo ESTADO (PENDIENTE, APROBADO, RECHAZADO)
     * @param p      PARÁMETROS DE PÁGINA
     * @return PÁGINA ORDENADA ASCENDENTE
     */
    Page<Producto> findByEstadoModCodigoOrderByCreadoEnAsc(String codigo, Pageable p);

    /**
     * LISTA PRODUCTOS CUYO ESTADO ESTÉ EN UN CONJUNTO DADO.
     *
     * @param codigos  LISTA DE ESTADOS
     * @param pageable PARÁMETROS DE PÁGINA
     * @return PÁGINA DE PRODUCTOS
     */
    Page<Producto> findByEstadoModCodigoIn(Collection<String> codigos, Pageable pageable);

    /**
     * LISTA PRODUCTOS POR ESTADOS Y ÚLTIMO MODERADOR QUE LOS REVISÓ.
     *
     * @param estados           ESTADOS A INCLUIR
     * @param ultimoModeradorId ID DEL MODERADOR
     * @param pageable          PARÁMETROS DE PÁGINA
     * @return PÁGINA DE PRODUCTOS
     */
    Page<Producto> findByEstadoModCodigoInAndUltimoModeradorId(
            java.util.Collection<String> estados,
            Integer ultimoModeradorId,
            Pageable pageable);
}
