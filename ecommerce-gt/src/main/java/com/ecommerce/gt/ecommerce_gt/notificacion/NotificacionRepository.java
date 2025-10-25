package com.ecommerce.gt.ecommerce_gt.notificacion;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.gt.ecommerce_gt.notificacion.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findTop20ByUsuarioIdOrderByCreadoEnDesc(Integer usuarioId);

    List<Notificacion> findByUsuarioIdAndCreadoEnAfterOrderByCreadoEnAsc(Integer usuarioId, Instant creadoEn);
}