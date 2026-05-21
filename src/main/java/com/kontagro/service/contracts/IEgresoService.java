package com.kontagro.service.contracts;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.dto.Class.IngresoDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface IEgresoService {

    EgresoDTO crearEgreso(EgresoDTO egresoDTO);

    EgresoDTO consultarEgreso(Integer id);

    EgresoDTO actualizarEgreso(EgresoDTO egresoDTO);

    Page<EgresoDTO> consultarEgreso(Pageable pageable);

    List<EgresoDTO> consultarEgresoPorFecha(
            @RequestParam LocalDate fecha_inicial,
            @RequestParam LocalDate fecha_final) throws BadRequestException;

    void eliminarEgreso(Integer id);
}
