package com.agroKont.kontAgro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "egresos")
public class Egreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_egresos")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_actividad", referencedColumnName = "id_actividad")
    private Actividad actividad;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "valor")
    private BigDecimal valor;


}
