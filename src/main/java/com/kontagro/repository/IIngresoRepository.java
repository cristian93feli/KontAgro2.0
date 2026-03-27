package com.kontagro.repository;

import com.kontagro.entities.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngresoRepository extends JpaRepository<Ingreso, Integer> {
}
