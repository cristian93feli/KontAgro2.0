package com.kontagro.repository;

import com.kontagro.entities.ActividadEconomica;
import com.kontagro.entities.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IActividadEconomicaRepository extends JpaRepository<ActividadEconomica, Integer> {
    List<ActividadEconomica> findByTipoMovimientoIsNotNullOrderByNombreActividadEconomicaAsc();
    Optional<ActividadEconomica> findFirstByTipoMovimiento(TipoMovimiento tipoMovimiento);
    Optional<ActividadEconomica> findFirstByNombreActividadEconomicaIgnoreCase(String nombreActividadEconomica);
}
