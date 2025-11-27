package com.agroKont.kontAgro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ingresos")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingresos")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_actividad", referencedColumnName = "id_actividad")
    private Actividad actividad;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "valor")
    private BigDecimal valor;


}
