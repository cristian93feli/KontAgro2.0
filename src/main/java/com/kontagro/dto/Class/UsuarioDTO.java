package com.kontagro.dto.Class;

import lombok.Data;

import java.io.Serializable;

@Data
public class UsuarioDTO implements Serializable {

    private Long id;
    private String usuario;

    // El usuario puede enviar la contraseña en el login, pero no se devolverá en el JSON de respuesta.
    @com.fasterxml.jackson.annotation.JsonProperty(
            access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String contrasena;
    private String nombres;
    private String apellidos;
}
