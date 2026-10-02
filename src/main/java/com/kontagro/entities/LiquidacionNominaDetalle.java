package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "liquidacion_nomina_detalle",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_liquidacion_pago",
                columnNames = {"id_nomina", "id_registrar_pago_trabajador"}
        )
)
public class LiquidacionNominaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_nomina")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nomina", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LiquidacionNomina liquidacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_registrar_pago_trabajador", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RegistrarPagoTrabajador pago;

    /* Snapshot contable de la tarea y valores al momento de liquidar. */
    @Column(name = "id_tarea_realizada", nullable = false)
    private Integer idTarea;

    @Column(name = "nombre_tarea", nullable = false)
    private String nombreTarea;

    @Column(name = "fecha_inicial", nullable = false)
    private LocalDate fechaInicial;

    @Column(name = "fecha_final", nullable = false)
    private LocalDate fechaFinal;

    @Column(name = "dias_trabajados", columnDefinition = "TEXT")
    private String diasTrabajados;

    @Column(name = "valor_pagar", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorPagar;

    @Column(name = "valor_descuentos", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorDescuentos;

    @Column(name = "observaciones")
    private String observaciones;
}
