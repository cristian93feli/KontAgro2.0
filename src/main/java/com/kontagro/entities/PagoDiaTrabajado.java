package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "pago_dias_trabajados",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pago_dia_trabajado",
                columnNames = {"id_registrar_pago_trabajador", "fecha"}
        )
)
public class PagoDiaTrabajado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_dia_trabajado")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_registrar_pago_trabajador", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RegistrarPagoTrabajador pago;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
}
