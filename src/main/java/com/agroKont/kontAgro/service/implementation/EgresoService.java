package com.agroKont.kontAgro.service.implementation;

import com.agroKont.kontAgro.entities.Egreso;
import com.agroKont.kontAgro.entities.Ingreso;
import com.agroKont.kontAgro.repository.IEgresoRepository;
import com.agroKont.kontAgro.service.contracts.IEgresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EgresoService implements IEgresoService {

    @Autowired
    private IEgresoRepository egresoRepository;

    @Override
    public ResponseEntity<Egreso> crearEgreso(Egreso egreso) {
        return new ResponseEntity<>(egresoRepository.save(egreso), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> consultarEgreso(Integer id) {
        Optional<Egreso> egresoOptional = egresoRepository.findById(id);

        if (egresoOptional.isPresent()) {
            return ResponseEntity.ok(egresoOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El egreso con ID " + id + " no fue encontrado.");
        }
    }

    @Override
    public ResponseEntity<Egreso> actualizarEgreso(Egreso egreso) {

        ResponseEntity<?> consulta = consultarEgreso(1);

        if (consulta.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(egresoRepository.save(egreso), HttpStatus.OK);
        }
        return ResponseEntity.status(consulta.getStatusCode()).build();
    }

    @Override
    public ResponseEntity<?> consultarEgreso() {
        List<Egreso> egresoOptional = egresoRepository.findAll();

        if (!egresoOptional.isEmpty()) {
            return ResponseEntity.ok(egresoOptional);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existen registros.");
        }
    }
}
