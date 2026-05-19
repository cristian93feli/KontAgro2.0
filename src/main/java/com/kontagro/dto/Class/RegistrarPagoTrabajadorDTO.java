package com.kontagro.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class RegistrarPagoTrabajadorDTO implements Serializable {

    private Integer id;
    private Integer idTrabajador;
    private Integer idActividad;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private String descuentos;
    private boolean pagado;
}