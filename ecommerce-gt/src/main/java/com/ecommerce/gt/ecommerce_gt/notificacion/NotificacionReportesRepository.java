package com.ecommerce.gt.ecommerce_gt.notificacion;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.NotificacionRowDTO;
import com.ecommerce.gt.ecommerce_gt.notificacion.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

/**
 * REPOSITORIO JPA PARA CONSULTAS DE REPORTES DE NOTIFICACIONES.
 * PERMITE GENERAR HISTORIALES DETALLADOS DE NOTIFICACIONES ENVIADAS.
 */
public interface NotificacionReportesRepository extends JpaRepository<Notificacion, Integer> {

  /**
   * OBTIENE EL HISTORIAL DE NOTIFICACIONES EN UN RANGO DE FECHAS.
   * DEVUELVE UNA LISTA DE NotificacionRowDTO CON DATOS DEL USUARIO Y ESTADO DE
   * ENVÍO.
   *
   * @param desde FECHA DE INICIO DEL RANGO
   * @param hasta FECHA FINAL DEL RANGO
   * @return LISTA DE NOTIFICACIONES EN EL PERIODO, ORDENADAS POR FECHA
   *         DESCENDENTE.
   */
  @Query("""
        select new com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.NotificacionRowDTO(
          n.id,
          n.usuarioId,
          coalesce(u.nombre, concat('Usuario #', n.usuarioId)),
          n.tipo,
          n.asunto,
          n.enviado,
          n.enviadoEn,
          n.creadoEn
        )
        from Notificacion n
        left join com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario u on u.id = n.usuarioId
        where n.creadoEn >= :desde and n.creadoEn < :hasta
        order by n.creadoEn desc
      """)
  List<NotificacionRowDTO> historial(Instant desde, Instant hasta);
}
