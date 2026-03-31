package com.kontagro.dto.Class;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EgresoDTO implements Serializable {

    private Integer id;
    private LocalDate fecha;
    private BigDecimal valor;
    private Integer idActividad; // solo el ID de la actividad

}
