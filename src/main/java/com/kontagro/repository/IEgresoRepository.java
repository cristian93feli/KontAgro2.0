package com.kontagro.repository;

import com.kontagro.entities.Egreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IEgresoRepository extends JpaRepository<Egreso, Integer> {
    boolean existsByActividad_IdActividad(Integer idActividad);
    List<Egreso> findByFechaBetween(LocalDate fechaInicial, LocalDate fechaFinal);
    List<Egreso> findByFechaBetweenOrderByFechaAsc(LocalDate fechaInicial, LocalDate fechaFinal);
}
