package com.kontagro.repository;

import com.kontagro.entities.LiquidacionNominaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ILiquidacionNominaDetalleRepository extends JpaRepository<LiquidacionNominaDetalle, Integer> {

    @Query("select d from LiquidacionNominaDetalle d where d.liquidacion.id = :idLiquidacion order by d.id")
    List<LiquidacionNominaDetalle> findByLiquidacionId(@Param("idLiquidacion") Integer idLiquidacion);

    boolean existsByLiquidacion_Id(Integer idLiquidacion);

    boolean existsByPago_Id(Integer idPago);

    @Query("select distinct d.pago.id from LiquidacionNominaDetalle d where d.pago.id in :ids")
    List<Integer> findPagoIdsConHistorial(@Param("ids") Collection<Integer> ids);

    @Query("select distinct d.liquidacion.id from LiquidacionNominaDetalle d where d.liquidacion.id in :ids")
    List<Integer> findLiquidacionIdsConDetalle(@Param("ids") Collection<Integer> ids);
}
