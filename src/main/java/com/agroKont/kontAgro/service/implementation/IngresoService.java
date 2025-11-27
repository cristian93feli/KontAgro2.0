package com.agroKont.kontAgro.service.implementation;

import com.agroKont.kontAgro.entities.Ingreso;
import com.agroKont.kontAgro.repository.IIngresoRepository;
import com.agroKont.kontAgro.service.contracts.IIngresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngresoService implements IIngresoService {

    @Autowired
    private IIngresoRepository ingresoRepository;

    @Override
    public ResponseEntity<Ingreso> crearIngreso(Ingreso ingreso) {
        return new ResponseEntity<>(ingresoRepository.save(ingreso), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> consultarIngreso(Integer id) {
        Optional<Ingreso> ingresoOptional = ingresoRepository.findById(id);

        if (ingresoOptional.isPresent()) {
            return ResponseEntity.ok(ingresoOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El ingreso con ID " + id + " no fue encontrado.");
        }
    }

    @Override
    public ResponseEntity<Ingreso> actualizarIngreso(Ingreso ingreso) {

        ResponseEntity<?> consulta = consultarIngreso(ingreso.getId());

        if (consulta.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(ingresoRepository.save(ingreso), HttpStatus.OK);
        }
        return ResponseEntity.status(consulta.getStatusCode()).build();
    }

    @Override
    public ResponseEntity<?> consultarIngreso() {
        List<Ingreso> ingresoOptional = ingresoRepository.findAll();

        if (!ingresoOptional.isEmpty()) {
            return ResponseEntity.ok(ingresoOptional);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existen registros.");
        }
    }
}
