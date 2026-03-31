package com.kontagro.service.implementation;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.dto.Converter.EgresoDTOConverter;
import com.kontagro.entities.Egreso;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IEgresoRepository;
import com.kontagro.service.contracts.IEgresoService;
import com.kontagro.utils.MensajesError;
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
public class EgresoService implements IEgresoService {

    private final IEgresoRepository iEgresoRepository;
    private final EgresoDTOConverter egresoDTOConverter;


    @Override
    public ResponseEntity<EgresoDTO> crearEgreso(EgresoDTO egresoDTO) {
        return new ResponseEntity<>(egresoDTOConverter.convertToDTO
                (iEgresoRepository.save(egresoDTOConverter.convertToEntity(egresoDTO))),  HttpStatus.OK);
    }

    @Override
    public EgresoDTO consultarEgreso(Integer id) {


        Optional<Egreso> resultado = iEgresoRepository.findById(id);

        if (resultado.isPresent()) {
            Egreso egreso = resultado.get();
            return egresoDTOConverter.convertToDTO(egreso);
        } else {
            throw new ResourceNotFoundException(String.format(MensajesError.ACTIVIDAD_NO_ENCONTRADA, id));
        }

    }





    @Override
    public ResponseEntity<EgresoDTO> actualizarEgreso(EgresoDTO egresoDTO) {

        EgresoDTO consulta = consultarEgreso(1);

        if (consulta!=null) {
            return new ResponseEntity<>(egresoDTOConverter.convertToDTO
                    (iEgresoRepository.save(egresoDTOConverter.convertToEntity(egresoDTO))), HttpStatus.OK);
        }
        return new ResponseEntity<>(consulta  , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> consultarEgreso() {
        List<Egreso> egresoOptional = iEgresoRepository.findAll();

        if (!egresoOptional.isEmpty()) {
            return ResponseEntity.ok(egresoOptional);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existen registros.");
        }
    }

    @Override
    public ResponseEntity<?> consultarEgresoPorFecha(LocalDate fecha_inicial, LocalDate fecha_final) throws BadRequestException {

        if (fecha_inicial == null || fecha_final == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas");
        }

        if (fecha_inicial.isAfter(fecha_final)) {
            throw new RuntimeException("La fecha inicial no puede ser mayor a la final");
        }
        List<Egreso> egresos = iEgresoRepository
                .findByFechaBetween(fecha_inicial, fecha_final);

        List<EgresoDTO> respuesta = egresos.stream()
                .map(egresoDTOConverter::convertToDTO)
                .toList();

        return ResponseEntity.ok(respuesta);
    }
}
