package com.agroKont.kontAgro.dto.Class;

import com.agroKont.kontAgro.entities.Actividad;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EgresoDTO implements Serializable {
    private Integer id;
    private Actividad actividad;
    private LocalDate fecha;
    private BigDecimal valor;
}
