package com.kontagro.service.contracts;

import com.kontagro.dto.Class.ActividadDTO;
import org.springframework.http.ResponseEntity;

public interface IActividadService {

    ResponseEntity<?> crearActividad(ActividadDTO actividad);

    ResponseEntity<?> consultarActividad(Integer id);

    ResponseEntity<ActividadDTO> actualizarActividad(ActividadDTO actividad);

    ResponseEntity<?> consultarActividad();
}
