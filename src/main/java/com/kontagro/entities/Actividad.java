package com.kontagro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Entity
@NoArgsConstructor
@Table(name = "actividades")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer idActividad;
    
    @Column(name = "nombre_actividad", nullable = false)
    private String nombreActividad;

}
