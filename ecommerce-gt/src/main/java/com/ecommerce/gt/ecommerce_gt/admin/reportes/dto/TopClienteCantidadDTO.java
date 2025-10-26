package com.ecommerce.gt.ecommerce_gt.admin.reportes.dto;

public class TopClienteCantidadDTO {
        private Integer clienteId;
        private String nombre;
        private Long cantidad;

        public TopClienteCantidadDTO(Integer clienteId, String nombre, Long cantidad) {
                this.clienteId = clienteId;
                this.nombre = nombre;
                this.cantidad = cantidad;
        }

        public Integer getClienteId() {
                return clienteId;
        }

        public String getNombre() {
                return nombre;
        }

        public Long getCantidad() {
                return cantidad;
        }
}
