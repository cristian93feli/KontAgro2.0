package com.kontagro.repository;

import com.kontagro.entities.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IActividadRepository extends JpaRepository<Actividad, Integer> {
}
