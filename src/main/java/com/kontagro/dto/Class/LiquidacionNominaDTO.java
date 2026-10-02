package com.kontagro.dto;

import com.kontagro.entities.enums.EstadoLiquidacion;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LiquidacionNominaDTO implements Serializable {

    private Integer id;
    private Integer idTrabajador;
    private LocalDate fechaInicialPagado;
    private LocalDate fechaFinalPagado;
    private BigDecimal valorTotalTrabajado;
    private BigDecimal valorTotalDescuentos;
    private BigDecimal valorTotalPagado;
    private LocalDateTime fechaLiquidacion;
    private LocalDateTime fechaAnulacion;
    private EstadoLiquidacion estado;
    private List<Integer> idsPagos;
    private boolean anulable;
}
