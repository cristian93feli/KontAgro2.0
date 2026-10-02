package com.kontagro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.kontagro.dto.Class.DocumentoSoporteDTO;

@Data
public class ContabilidadResumenDTO {
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private LocalDateTime generadoEn;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresosOperativos;
    private BigDecimal totalNominaPagada;
    private BigDecimal totalGastos;
    private BigDecimal resultadoNeto;
    private BigDecimal pagosPendientes;
    private BigDecimal resultadoProyectado;
    private List<MovimientoContableDTO> movimientos;
    private List<ObligacionPendienteDTO> obligacionesPendientes;
    private List<DocumentoSoporteDTO> documentosSoporte;
}
