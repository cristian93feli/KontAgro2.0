package com.kontagro.entities;

import com.kontagro.entities.enums.TipoMovimiento;
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

    /**
     * Clasificación financiera de la categoría. Las actividades heredan este
     * valor, evitando que el usuario tenga que seleccionar Ingreso/Egreso dos veces.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", length = 20)
    private TipoMovimiento tipoMovimiento;
}
