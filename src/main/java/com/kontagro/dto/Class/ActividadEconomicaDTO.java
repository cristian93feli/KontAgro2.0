package com.kontagro.dto.Class;

import com.kontagro.entities.enums.TipoMovimiento;
import lombok.Data;

import java.io.Serializable;

@Data
public class ActividadEconomicaDTO implements Serializable {
    private Integer id;
    private String nombreActividadEconomica;
    private TipoMovimiento tipoMovimiento;
}
