package com.ecommerce.gt.ecommerce_gt.notificacion;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.NotificacionRowDTO;
import com.ecommerce.gt.ecommerce_gt.notificacion.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface NotificacionReportesRepository extends JpaRepository<Notificacion, Integer> {

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