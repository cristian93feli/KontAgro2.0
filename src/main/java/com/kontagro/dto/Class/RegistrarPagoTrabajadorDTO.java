package com.kontagro.dto;

import com.kontagro.entities.enums.EstadoPago;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class RegistrarPagoTrabajadorDTO implements Serializable {
    private Integer id;
    private Integer idTrabajador;
    private Integer idTarea;
    private String nombreTarea;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private List<LocalDate> diasTrabajados;
    private BigDecimal valorPagar;
    private BigDecimal valorDescuentos;
    private String observaciones;
    private EstadoPago estado;
    private boolean tieneHistorialNomina;
}
