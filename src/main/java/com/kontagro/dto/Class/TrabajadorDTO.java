package com.kontagro.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class TrabajadorDTO implements Serializable {

    private Integer id;
    private String nombre;
}