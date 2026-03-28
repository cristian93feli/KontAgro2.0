package com.kontagro.service.implementation;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Converter.IngresoDTOConverter;
import com.kontagro.entities.Ingreso;
import com.kontagro.repository.IIngresoRepository;
import com.kontagro.service.contracts.IIngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngresoService implements IIngresoService {

    private final IIngresoRepository iIngresoRepository;
    private final IngresoDTOConverter ingresoDTOConverter;

    @Override
    public ResponseEntity<IngresoDTO> crearIngreso(IngresoDTO ingresoDTO) {
        return new ResponseEntity<>(ingresoDTOConverter.convertToDTO
                (iIngresoRepository.save(ingresoDTOConverter.convertToEntity(ingresoDTO))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> consultarIngreso(Integer id) {
        Optional<Ingreso> ingresoOptional = iIngresoRepository.findById(id);

        if (ingresoOptional.isPresent()) {
            return ResponseEntity.ok(ingresoOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El ingreso con ID " + id + " no fue encontrado.");
        }
    }

    @Override
    public ResponseEntity<IngresoDTO> actualizarIngreso(IngresoDTO ingresoDTO) {

        ResponseEntity<?> consulta = consultarIngreso(1);

        if (consulta.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(ingresoDTOConverter.convertToDTO
                    (iIngresoRepository.save(ingresoDTOConverter.convertToEntity(ingresoDTO))), HttpStatus.OK);
        }
        return ResponseEntity.status(consulta.getStatusCode()).build();
    }

    @Override
    public ResponseEntity<?> consultarIngreso() {
        List<Ingreso> ingresoOptional = iIngresoRepository.findAll();

        if (!ingresoOptional.isEmpty()) {
            return ResponseEntity.ok(ingresoOptional);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existen registros.");
        }
    }
}

