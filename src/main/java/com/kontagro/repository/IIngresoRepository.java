package com.kontagro.repository;

import com.kontagro.entities.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface IIngresoRepository extends JpaRepository<Ingreso, Integer> {

    List<Ingreso> findByFechaBetween(LocalDate fechaInicial, LocalDate fechaFinal);
}
