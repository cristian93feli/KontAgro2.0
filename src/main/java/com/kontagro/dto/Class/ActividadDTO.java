package com.kontagro.dto.Class;

import com.kontagro.entities.enums.TipoMovimiento;
import lombok.Data;

import java.io.Serializable;

@Data
public class ActividadDTO implements Serializable {

    private Integer idActividad;
    private String nombreActividad;
    private Integer idActividadEconomica;
    private String nombreActividadEconomica;
    private TipoMovimiento tipoMovimiento;
}
