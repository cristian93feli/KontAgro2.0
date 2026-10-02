package com.kontagro.service.contracts;

import com.kontagro.dto.Class.EgresoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IEgresoService {

    EgresoDTO crearEgreso(EgresoDTO egresoDTO);

    EgresoDTO consultarEgreso(Integer id);

    EgresoDTO actualizarEgreso(EgresoDTO egresoDTO);

    Page<EgresoDTO> consultarEgreso(Pageable pageable);

    List<EgresoDTO> consultarEgresoPorFecha(LocalDate fechaInicial, LocalDate fechaFinal);

    void eliminarEgreso(Integer id);
}
