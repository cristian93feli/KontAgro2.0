package com.kontagro.repository;

import com.kontagro.entities.LiquidacionNomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILiquidacionNominaRepository extends JpaRepository<LiquidacionNomina, Integer> {

}