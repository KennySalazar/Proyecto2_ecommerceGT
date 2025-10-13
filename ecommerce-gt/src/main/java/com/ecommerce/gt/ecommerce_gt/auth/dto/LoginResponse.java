package com.ecommerce.gt.ecommerce_gt.auth.dto;

public class LoginResponse {
    private Integer id;
    private String nombre;
    private String correo;
    private String rolCodigo;
    private String token;

    public LoginResponse(Integer id, String nombre, String correo, String rolCodigo, String token) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rolCodigo = rolCodigo;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRolCodigo() {
        return rolCodigo;
    }

    public String getToken() {
        return token;
    }
}