package com.kontagro.repository;

import com.kontagro.entities.Trabajador;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    /**
     * Serializa operaciones financieras del mismo trabajador dentro de una
     * transacción. Esto evita que dos solicitudes simultáneas registren la
     * misma jornada antes de que la otra termine de confirmar sus cambios.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trabajador t where t.id = :id")
    Optional<Trabajador> findByIdForUpdate(@Param("id") Integer id);
}
