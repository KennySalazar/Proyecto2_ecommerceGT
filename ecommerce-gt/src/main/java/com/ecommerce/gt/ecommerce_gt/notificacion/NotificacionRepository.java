package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.gt.ecommerce_gt.notificacion.entity.Notificacion;

/**
 * REPOSITORIO JPA PARA GESTIONAR LAS NOTIFICACIONES DE USUARIOS.
 * PERMITE CONSULTAR LAS MÁS RECIENTES O LAS GENERADAS DESPUÉS DE UNA FECHA
 * ESPECÍFICA.
 */
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    /**
     * OBTIENE LAS 20 NOTIFICACIONES MÁS RECIENTES DE UN USUARIO.
     * ORDENADAS DE MÁS NUEVA A MÁS ANTIGUA.
     *
     * @param usuarioId ID DEL USUARIO.
     * @return LISTA DE LAS ÚLTIMAS 20 NOTIFICACIONES.
     */
    List<Notificacion> findTop20ByUsuarioIdOrderByCreadoEnDesc(Integer usuarioId);

    /**
     * OBTIENE LAS NOTIFICACIONES DE UN USUARIO CREADAS DESPUÉS DE UNA FECHA DADA.
     * ÚTIL PARA ACTUALIZAR LISTADOS O STREAMS EN TIEMPO REAL.
     *
     * @param usuarioId ID DEL USUARIO.
     * @param creadoEn  FECHA DESDE LA CUAL BUSCAR (Instant).
     * @return LISTA DE NOTIFICACIONES MÁS NUEVAS QUE ESA FECHA.
     */
    List<Notificacion> findByUsuarioIdAndCreadoEnAfterOrderByCreadoEnAsc(Integer usuarioId, Instant creadoEn);
}
