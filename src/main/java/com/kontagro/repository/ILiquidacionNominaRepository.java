package com.kontagro.repository;

import com.kontagro.entities.LiquidacionNomina;
import com.kontagro.entities.enums.EstadoLiquidacion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ILiquidacionNominaRepository extends JpaRepository<LiquidacionNomina, Integer> {

    boolean existsByTrabajador_Id(Integer idTrabajador);

    List<LiquidacionNomina> findByEstadoAndFechaLiquidacionGreaterThanEqualAndFechaLiquidacionLessThanOrderByFechaLiquidacionAsc(
            EstadoLiquidacion estado,
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinalExclusiva
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LiquidacionNomina l where l.id = :id")
    Optional<LiquidacionNomina> findByIdForUpdate(@Param("id") Integer id);
}
