package com.kontagro.service.contracts;

import com.kontagro.dto.Class.EgresoDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface IEgresoService {

    EgresoDTO crearEgreso(EgresoDTO egresoDTO);

    EgresoDTO consultarEgreso(Integer id);

    EgresoDTO actualizarEgreso(EgresoDTO egresoDTO);

    List<EgresoDTO> consultarEgreso();

    List<EgresoDTO> consultarEgresoPorFecha(
            @RequestParam LocalDate fecha_inicial,
            @RequestParam LocalDate fecha_final) throws BadRequestException;
}
