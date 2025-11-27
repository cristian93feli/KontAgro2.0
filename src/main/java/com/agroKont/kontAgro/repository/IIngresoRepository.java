package com.agroKont.kontAgro.repository;

import com.agroKont.kontAgro.entities.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngresoRepository extends JpaRepository<Ingreso, Integer> {
}
