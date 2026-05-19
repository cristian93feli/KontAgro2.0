package com.kontagro.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LiquidacionNominaDTO implements Serializable {

    private Integer id;
    private Integer idTrabajador;
    private LocalDate fechaInicialPagado;
    private LocalDate fechaFinalPagado;
    private BigDecimal valorTotalTrabajado;
    private BigDecimal valorTotalDescuentos;
    private BigDecimal valorTotalPagado;
}