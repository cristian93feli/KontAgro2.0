package com.kontagro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObligacionPendienteDTO {
    private Integer idPago;
    private String trabajador;
    private String tarea;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private List<LocalDate> diasTrabajados;
    private BigDecimal valorPagar;
    private BigDecimal descuentos;
    private BigDecimal valorNeto;
}
