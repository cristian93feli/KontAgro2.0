package com.kontagro.dto.Class;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class IngresoDTO implements Serializable {

    private LocalDate fecha;
    private BigDecimal valor;
    private Integer idActividad;
}
