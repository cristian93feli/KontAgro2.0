package com.kontagro.dto.Class;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngresoporActividadDTO implements Serializable {
    private Integer idIngresos;
    private LocalDate fecha;
    private BigDecimal valor;
    private String nombreActividad;

}
