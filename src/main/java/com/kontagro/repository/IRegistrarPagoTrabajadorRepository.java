package com.kontagro.repository;

import com.kontagro.entities.RegistrarPagoTrabajador;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface IRegistrarPagoTrabajadorRepository extends JpaRepository<RegistrarPagoTrabajador, Integer> {

    List<RegistrarPagoTrabajador> findByTrabajador_IdAndPagadoFalseOrderByFechaInicialAsc(Integer idTrabajador);

    List<RegistrarPagoTrabajador> findByPagadoFalseAndFechaInicialLessThanEqualAndFechaFinalGreaterThanEqualOrderByFechaInicialAsc(
            LocalDate fechaFin,
            LocalDate fechaInicio
    );

    boolean existsByTrabajador_Id(Integer idTrabajador);

    boolean existsByTarea_Id(Integer idTarea);

    @Query("""
            select distinct d.fecha
            from RegistrarPagoTrabajador p
            join p.diasTrabajados d
            where p.trabajador.id = :idTrabajador
              and d.fecha in :fechas
            order by d.fecha
            """)
    List<LocalDate> findDiasRegistradosPorTrabajador(
            @Param("idTrabajador") Integer idTrabajador,
            @Param("fechas") Collection<LocalDate> fechas
    );

    @Query("""
            select distinct d.fecha
            from RegistrarPagoTrabajador p
            join p.diasTrabajados d
            where p.trabajador.id = :idTrabajador
              and p.id <> :idPagoExcluir
              and d.fecha in :fechas
            order by d.fecha
            """)
    List<LocalDate> findDiasRegistradosPorTrabajadorExcluyendoPago(
            @Param("idTrabajador") Integer idTrabajador,
            @Param("fechas") Collection<LocalDate> fechas,
            @Param("idPagoExcluir") Integer idPagoExcluir
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from RegistrarPagoTrabajador p where p.id in :ids order by p.id")
    List<RegistrarPagoTrabajador> findAllByIdForUpdate(@Param("ids") Collection<Integer> ids);
}
