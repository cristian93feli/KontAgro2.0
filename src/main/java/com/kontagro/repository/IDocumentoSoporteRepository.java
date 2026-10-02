package com.kontagro.repository;

import com.kontagro.entities.DocumentoSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDocumentoSoporteRepository extends JpaRepository<DocumentoSoporte, Long> {
    List<DocumentoSoporte> findByIngreso_IdOrderByFechaCargaDesc(Integer idIngreso);
    List<DocumentoSoporte> findByEgreso_IdOrderByFechaCargaDesc(Integer idEgreso);
    List<DocumentoSoporte> findByIngreso_IdIn(List<Integer> idsIngresos);
    List<DocumentoSoporte> findByEgreso_IdIn(List<Integer> idsEgresos);
}
