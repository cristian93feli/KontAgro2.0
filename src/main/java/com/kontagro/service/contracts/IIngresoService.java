package com.kontagro.service.contracts;

import com.kontagro.dto.Class.IngresoDTO;;
import org.springframework.http.ResponseEntity;


public interface IIngresoService {

    ResponseEntity<IngresoDTO> crearIngreso(IngresoDTO ingresoDTO);

    ResponseEntity<?> consultarIngreso(Integer id);

    ResponseEntity<IngresoDTO> actualizarIngreso(IngresoDTO ingresoDTO);

    ResponseEntity<?> consultarIngreso();

}

