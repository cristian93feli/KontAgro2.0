package com.kontagro.repository;

import com.kontagro.entities.ActividadEconomica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IActividadEconomicaRepository extends JpaRepository<ActividadEconomica, Integer> {
}