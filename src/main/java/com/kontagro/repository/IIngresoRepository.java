package com.kontagro.repository;

import com.kontagro.dto.Class.IngresoporActividadDTO;
import com.kontagro.entities.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface IIngresoRepository extends JpaRepository<Ingreso, Integer> {

//    List<Ingreso> findByFechaBetween(LocalDate fechaInicial, LocalDate fechaFinal);

    @Query(value = "SELECT i.id_ingresos AS idIngresos,  i.fecha AS fecha, i.valor AS valor, a.nombre_actividad AS NombreActividad\n" +
            "FROM ingresos AS i \n" +
            "INNER JOIN  actividades AS a\n" +
            "on i.id_actividad = a.id_actividad\n" +
            "WHERE i.fecha BETWEEN :inicio AND :fin", nativeQuery = true)
    List<IngresoporActividadDTO> findFechas(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
