package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "registrar_pago_trabajador")
public class RegistrarPagoTrabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registrar_pago_trabajador")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_trabajador", referencedColumnName = "id_trabajador", nullable = false)
    private Trabajador trabajador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tarea_realizada", nullable = false)
    private TareasRealizadas tarea;

    @Column(name = "fecha_inicial", nullable = false)
    private LocalDate fechaInicial;

    @Column(name = "fecha_final", nullable = false)
    private LocalDate fechaFinal;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha ASC")
    private List<PagoDiaTrabajado> diasTrabajados = new ArrayList<>();

    @Column(name = "descuentos", nullable = false)
    private String observaciones;

    @Column(name = "valor_pagar", precision = 19, scale = 2)
    private BigDecimal valorPagar;

    @Column(name = "valor_descuentos", precision = 19, scale = 2)
    private BigDecimal valorDescuentos;

    @Column(name = "pagado", nullable = false)
    private boolean pagado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        fechaCreacion = ahora;
        fechaActualizacion = ahora;
        normalizarValoresOpcionales();
    }

    @PreUpdate
    void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
        normalizarValoresOpcionales();
    }

    private void normalizarValoresOpcionales() {
        if (valorDescuentos == null) {
            valorDescuentos = BigDecimal.ZERO;
        }
        if (observaciones == null) {
            observaciones = "";
        }
    }
}
