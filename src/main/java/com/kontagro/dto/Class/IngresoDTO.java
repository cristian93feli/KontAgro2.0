package com.kontagro.dto.Class;

import com.kontagro.entities.Actividad;

import java.io.Serializable;
import java.time.LocalDate;

public class IngresoDTO implements Serializable {

    private LocalDate fecha;
    private Actividad actividad;

}
