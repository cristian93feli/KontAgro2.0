package com.kontagro.repository;

import com.kontagro.entities.TareasRealizadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITareasRealizadasRepository extends JpaRepository<TareasRealizadas, Integer> {
}