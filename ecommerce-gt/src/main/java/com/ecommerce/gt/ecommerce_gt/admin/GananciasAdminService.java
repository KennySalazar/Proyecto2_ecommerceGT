package com.ecommerce.gt.ecommerce_gt.admin;

import com.ecommerce.gt.ecommerce_gt.admin.dto.GananciasAdminDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;

/**
 * SERVICIO DE NEGOCIO PARA EL CÁLCULO DE GANANCIAS DEL ADMIN.
 * SE ENCARGA DE OBTENER EL TOTAL DE COMISIONES GENERADAS EN UN RANGO DE FECHAS.
 */
@Service
@RequiredArgsConstructor
public class GananciasAdminService {

    /**
     * REPOSITORIO DE PEDIDOS UTILIZADO PARA CALCULAR LA SUMA DE COMISIONES.
     */
    private final PedidoRepository pedidoRepo;

    /**
     * CALCULA EL TOTAL DE GANANCIAS (COMISIONES) EN UN RANGO DE FECHAS.
     * 
     * 
     * EL PARÁMETRO {@code soloEntregados} SE RESERVA PARA FUTURAS IMPLEMENTACIONES
     * EN LAS QUE SE FILTRARÁN PEDIDOS SEGÚN SU ESTADO DE ENTREGA.
     *
     * @param desdeIso       FECHA DE INICIO EN FORMATO ISO (yyyy-MM-dd). PUEDE SER
     *                       NULA O VACÍA.
     * @param hastaIso       FECHA FINAL EN FORMATO ISO (yyyy-MM-dd). PUEDE SER NULA
     *                       O VACÍA.
     * @param soloEntregados INDICA SI SOLO SE DEBEN CONTAR PEDIDOS ENTREGADOS
     * 
     * @return OBJETO GananciasAdminDTO CON EL TOTAL DE COMISIONES CALCULADAS.
     */
    public GananciasAdminDTO total(String desdeIso, String hastaIso, Boolean soloEntregados) {
        // SI EL PARÁMETRO ES NULO, SE ASUME TRUE POR DEFECTO.
        var solo = soloEntregados != null ? soloEntregados : true;

        // CONVERSIÓN DE LAS FECHAS DE TEXTO A INSTANTES EN UTC.
        var desde = (desdeIso == null || desdeIso.isBlank())
                ? null
                : LocalDate.parse(desdeIso).atStartOfDay().toInstant(ZoneOffset.UTC);
        var hasta = (hastaIso == null || hastaIso.isBlank())
                ? null
                : LocalDate.parse(hastaIso).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        // OBTIENE LA SUMA DE COMISIONES EN EL RANGO INDICADO.
        Long suma = pedidoRepo.sumaComisionPorRango(
                desde != null ? desde : Instant.EPOCH,
                hasta != null ? hasta : Instant.now());

        if (suma == null)
            suma = 0L;

        return new GananciasAdminDTO(suma);
    }
}
