package com.kontagro.repository;

import com.kontagro.entities.TareasRealizadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITareasRealizadasRepository extends JpaRepository<TareasRealizadas, Integer> {
    List<TareasRealizadas> findAllByOrderByNombreAsc();
    List<TareasRealizadas> findByActivoTrueOrderByNombreAsc();
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
