package com.kontagro.repository;

import com.kontagro.entities.Egreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IEgresoRepository extends JpaRepository<Egreso, Integer> {

    List<Egreso> findByFechaBetween(LocalDate fecha_inicial, LocalDate fecha_final);
}
