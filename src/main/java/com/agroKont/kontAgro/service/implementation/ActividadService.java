package com.agroKont.kontAgro.service.implementation;

import com.agroKont.kontAgro.entities.Actividad;
import com.agroKont.kontAgro.entities.Egreso;
import com.agroKont.kontAgro.repository.IActividadRepository;
import com.agroKont.kontAgro.service.contracts.IActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService implements IActividadService {

    @Autowired
    private IActividadRepository actividadRepository;

    @Override
    public ResponseEntity<Actividad> crearActividad(Actividad actividad) {
        return new ResponseEntity<>(actividadRepository.save(actividad), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> consultarActividad(Integer id) {

        Optional<Actividad> actividadOptional = actividadRepository.findById(id);

        if (actividadOptional.isPresent()) {
            return ResponseEntity.ok(actividadOptional.get());
        } else {
            String mensaje = "La actividad con ID " + id + " no fue encontrada.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }
    }

    @Override
    public ResponseEntity<Actividad> actualizarActividad(Actividad actividad) {

        ResponseEntity<?> consulta = consultarActividad(actividad.getIdActividad());

        if (consulta.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(actividadRepository.save(actividad), HttpStatus.OK);
        }
        return ResponseEntity.status(consulta.getStatusCode()).build();
    }

    @Override
    public ResponseEntity<?> consultarActividad() {
        List<Actividad> actividadOptional = actividadRepository.findAll();

        if (!actividadOptional.isEmpty()) {
            return ResponseEntity.ok(actividadOptional);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existen registros.");
        }
    }
}
