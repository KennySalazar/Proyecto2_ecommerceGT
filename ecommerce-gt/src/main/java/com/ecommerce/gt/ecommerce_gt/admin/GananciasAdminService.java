package com.ecommerce.gt.ecommerce_gt.admin;

import com.ecommerce.gt.ecommerce_gt.admin.dto.GananciasAdminDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@RequiredArgsConstructor
public class GananciasAdminService {

    private final PedidoRepository pedidoRepo;

    public GananciasAdminDTO total(String desdeIso, String hastaIso, Boolean soloEntregados) {
        var solo = soloEntregados != null ? soloEntregados : true;

        // Rango opcional
        var desde = (desdeIso == null || desdeIso.isBlank())
                ? null
                : LocalDate.parse(desdeIso).atStartOfDay().toInstant(ZoneOffset.UTC);
        var hasta = (hastaIso == null || hastaIso.isBlank())
                ? null
                : LocalDate.parse(hastaIso).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        Long suma = pedidoRepo.sumaComisionPorRango(
                desde != null ? desde : Instant.EPOCH,
                hasta != null ? hasta : Instant.now());
        if (suma == null)
            suma = 0L;

        return new GananciasAdminDTO(suma);
    }
}