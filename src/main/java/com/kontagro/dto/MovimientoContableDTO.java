package com.kontagro.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MovimientoContableDTO {
    private LocalDate fecha;
    private String origen;
    private String concepto;
    private String detalle;
    private BigDecimal valor;
    private String tercero;
    private String documento;
    private int soportes;

    public MovimientoContableDTO(LocalDate fecha, String origen, String concepto, String detalle, BigDecimal valor) {
        this.fecha = fecha;
        this.origen = origen;
        this.concepto = concepto;
        this.detalle = detalle;
        this.valor = valor;
    }
}
