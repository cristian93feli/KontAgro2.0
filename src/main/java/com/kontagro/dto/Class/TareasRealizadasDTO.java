package com.kontagro.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TareasRealizadasDTO implements Serializable {

    private Integer id;
    private Integer idActividad;
    private LocalDate fecha;
}