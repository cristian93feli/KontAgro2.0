package com.kontagro.service.contracts;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.entities.Actividad;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IActividadService {

    ActividadDTO crearActividad(ActividadDTO actividad);

    ActividadDTO consultarActividad(Integer id);

    ActividadDTO actualizarActividad(ActividadDTO actividad);

    List<ActividadDTO> consultarActividad();
}
