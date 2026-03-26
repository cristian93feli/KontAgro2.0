package com.agroKont.kontAgro.service.contracts;

import com.agroKont.kontAgro.dto.Class.ActividadDTO;
import com.agroKont.kontAgro.entities.Actividad;
import org.springframework.http.ResponseEntity;

public interface IActividadService {

    ResponseEntity<?> crearActividad(ActividadDTO actividad);

    ResponseEntity<?> consultarActividad(Integer id);

    ResponseEntity<ActividadDTO> actualizarActividad(ActividadDTO actividad);

    ResponseEntity<?> consultarActividad();
}
