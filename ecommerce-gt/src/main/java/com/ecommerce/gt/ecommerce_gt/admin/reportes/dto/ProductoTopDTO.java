package com.ecommerce.gt.ecommerce_gt.admin.reportes.dto;

public class ProductoTopDTO {
        private Integer productoId;
        private String nombre;
        private Long unidadesVendidas;
        private Long totalVendido;

        public ProductoTopDTO(Integer productoId, String nombre, Long unidadesVendidas, Long totalVendido) {
                this.productoId = productoId;
                this.nombre = nombre;
                this.unidadesVendidas = unidadesVendidas;
                this.totalVendido = totalVendido;
        }

        public Integer getProductoId() {
                return productoId;
        }

        public String getNombre() {
                return nombre;
        }

        public Long getUnidadesVendidas() {
                return unidadesVendidas;
        }

        public Long getTotalVendido() {
                return totalVendido;
        }
}