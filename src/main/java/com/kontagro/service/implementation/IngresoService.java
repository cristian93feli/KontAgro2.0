package com.kontagro.service.implementation;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Converter.IngresoDTOConverter;
import com.kontagro.entities.Ingreso;
import com.kontagro.repository.IIngresoRepository;
import com.kontagro.service.contracts.IIngresoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngresoService implements IIngresoService {

    private final IIngresoRepository iIngresoRepository;
    private final IngresoDTOConverter ingresoDTOConverter;

    @Override
    public ResponseEntity<IngresoDTO> crearIngreso(IngresoDTO ingresoDTO) {
        Ingreso ingre = ingresoDTOConverter.convertToEntity(ingresoDTO);
        ingre = iIngresoRepository.save(ingre);
        ingresoDTO = ingresoDTOConverter.convertToDTO(ingre);
        return new ResponseEntity<>(ingresoDTO, HttpStatus.OK);
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

    @Override
    public List<IngresoDTO> consultarIngresoPorFecha(LocalDate fechaInicial, LocalDate fechaFinal)
            throws BadRequestException {

        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas");
        }

        if (fechaInicial.isAfter(fechaFinal)) {
            throw new RuntimeException("La fecha inicial no puede ser mayor a la final");
        }
        List<Ingreso> ingresos = iIngresoRepository.findByFechaBetween(fechaInicial, fechaFinal);

        List<IngresoDTO> respuesta = ingresos.stream()
                .map(ingresoDTOConverter::convertToDTO)
                .toList();

        return respuesta;
    }

    @Override
    public ResponseEntity<String> eliminarIngreso(Integer id) {
        if (iIngresoRepository.existsById(id)) {
            iIngresoRepository.deleteById(id);
            return ResponseEntity.ok("Registro de ingreso eliminado exitosamente");
        } else {
            String mensaje = "El registro de ingreso con ID '" + id + "' no existe.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }
    }
}

