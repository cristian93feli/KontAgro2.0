package com.kontagro.service.contracts;

import com.kontagro.dto.Class.IngresoDTO;;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;


public interface IIngresoService {

    ResponseEntity<IngresoDTO> crearIngreso(IngresoDTO ingresoDTO);

    ResponseEntity<?> consultarIngreso(Integer id);

    ResponseEntity<IngresoDTO> actualizarIngreso(IngresoDTO ingresoDTO);

    ResponseEntity<?> consultarIngreso();

    List<IngresoDTO> consultarIngresoPorFecha(LocalDate fechaInicial,LocalDate fechaFinal)
            throws BadRequestException;

}

