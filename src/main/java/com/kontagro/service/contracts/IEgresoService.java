package com.kontagro.service.contracts;

import com.kontagro.dto.Class.EgresoDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public interface IEgresoService {

    ResponseEntity<EgresoDTO> crearEgreso(EgresoDTO egresoDTO);

    ResponseEntity<?> consultarEgreso(Integer id);

    ResponseEntity<EgresoDTO> actualizarEgreso(EgresoDTO egresoDTO);

    ResponseEntity<?> consultarEgreso();

    ResponseEntity<?> consultarEgresoPorFecha(
            @RequestParam LocalDate fecha_inicial,
            @RequestParam LocalDate fecha_final) throws BadRequestException;
}
