package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "registrar_pago_trabajador")
public class RegistrarPagoTrabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registrar_pago_trabajador")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_trabajador", referencedColumnName = "id_trabajador", nullable = false)
    private Trabajador trabajador;
    @ManyToOne
    @Column(name = "id_actividad", nullable = false)
    private Actividad actividad;
    @Column(name = "fecha_inicial", nullable = false)
    private LocalDate fechaInicial;
    @Column(name = "fecha_final", nullable = false)
    private LocalDate fechaFinal;
    @Column(name = "descuentos", nullable = false)
    private String descuentos;
    @Column(name = "pagado", nullable = false)
    private boolean pagado;

}
