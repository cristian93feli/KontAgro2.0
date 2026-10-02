package com.kontagro.entities;

import com.kontagro.entities.enums.EstadoLiquidacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "liquidacion_nomina")
public class LiquidacionNomina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nomina")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_trabajador", referencedColumnName = "id_trabajador", nullable = false)
    private Trabajador trabajador;

    @Column(name = "fecha_inicial_pagado", nullable = false)
    private LocalDate fechaInicialPagado;

    @Column(name = "fecha_final_pagado", nullable = false)
    private LocalDate fechaFinalPagado;

    @Column(name = "valor_total_trabajado", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalTrabajado;

    @Column(name = "valor_total_descuentos", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalDescuentos;

    @Column(name = "valor_total_pagado", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalPagado;

    @Column(name = "fecha_liquidacion")
    private LocalDateTime fechaLiquidacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoLiquidacion estado;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @PrePersist
    void prePersist() {
        if (fechaLiquidacion == null) {
            fechaLiquidacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoLiquidacion.ACTIVA;
        }
    }
}
