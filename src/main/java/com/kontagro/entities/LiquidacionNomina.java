package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "liquidacion_nomina")
public class LiquidacionNomina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nomina")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_trabajador", referencedColumnName = "id_trabajador", nullable = false)
    private Trabajador trabajador;
    @Column(name = "fecha_inicial_pagado", nullable = false)
    private LocalDate fechaInicialPagado;
    @Column(name = "fecha_final_pagado", nullable = false)
    private LocalDate fechaFinalPagado;
    @Column(name = "valor_total_trabajado", nullable = false)
    private BigDecimal valorTotalTrabajado;
    @Column(name = "valor_total_descuentos", nullable = false)
    private BigDecimal valorTotalDescuentos;
    @Column(name = "valor_total_pagado", nullable = false)
    private BigDecimal valorTotalPagado;

}
