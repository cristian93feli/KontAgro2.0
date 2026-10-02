package com.kontagro.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TareasRealizadasDTO implements Serializable {
    private Integer id;
    private String nombre;
    private String descripcion;
    private boolean activo;
}
