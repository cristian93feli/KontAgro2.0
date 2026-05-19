package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tareas_realizadas")
public class TareasRealizadas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea_realizada")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_actividad", referencedColumnName = "id_actividad", nullable = false)
    private Actividad actividad;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;


}
