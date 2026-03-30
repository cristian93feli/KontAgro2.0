package com.kontagro.dto.Class;

import lombok.Data;

import java.io.Serializable;

@Data
public class UsuarioDTO implements Serializable {

    private Long id;
    private String usuario;
    private String contrasena;
    private String nombres;
    private String apellidos;
}
