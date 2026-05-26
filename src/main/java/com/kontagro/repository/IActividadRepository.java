package com.kontagro.repository;

import com.kontagro.entities.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IActividadRepository extends JpaRepository<Actividad, Integer> {

    List<Actividad> findByActividadEconomicaId(Integer idActividadEconomica);
}
