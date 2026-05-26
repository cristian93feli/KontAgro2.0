package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@NoArgsConstructor
@Table(name = "actividad_economica")
public class ActividadEconomica {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_actividad_economica")
        private Integer id;
        @Column(name = "nombre_actividad_economica", nullable = false)
        private String nombreActividadEconomica;
}
