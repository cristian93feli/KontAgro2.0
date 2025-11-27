package com.agroKont.kontAgro.service.contracts;

import com.agroKont.kontAgro.entities.Actividad;
import org.springframework.http.ResponseEntity;

public interface IActividadService {

    ResponseEntity<Actividad> crearActividad(Actividad actividad);

    ResponseEntity<?> consultarActividad(Integer id);

    ResponseEntity<Actividad> actualizarActividad(Actividad actividad);

    ResponseEntity<?> consultarActividad();
}
