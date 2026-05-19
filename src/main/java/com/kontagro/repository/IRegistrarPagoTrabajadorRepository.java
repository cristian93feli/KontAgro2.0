package com.kontagro.repository;

import com.kontagro.entities.RegistrarPagoTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRegistrarPagoTrabajadorRepository extends JpaRepository<RegistrarPagoTrabajador, Integer> {
}