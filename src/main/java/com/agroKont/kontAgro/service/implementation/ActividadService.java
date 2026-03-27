package com.agroKont.kontAgro.service.implementation;

import com.agroKont.kontAgro.dto.Class.ActividadDTO;
import com.agroKont.kontAgro.dto.Converter.ActividadDTOConverter;
import com.agroKont.kontAgro.entities.Actividad;
import com.agroKont.kontAgro.repository.IActividadRepository;
import com.agroKont.kontAgro.service.contracts.IActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActividadService implements IActividadService {

    private final IActividadRepository actividadRepository;
    private final ActividadDTOConverter actividadDTOConverter;

    @Override
    public ResponseEntity<?> crearActividad(ActividadDTO actividadDTO) {
        List<Actividad> actividades  = actividadRepository.findAll();
        boolean existeId = false;
        for (int i = 0; i < actividades.size() ; i++) {

            if (actividadDTO.getIdActividad()==actividades.get(i).getIdActividad()){
                existeId = true;
                break;
            }
        }
        if (existeId){
            String mensaje = "La actividad con ID " + actividadDTO.getIdActividad() + " ya existe.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }else {

            return new ResponseEntity<>(actividadDTOConverter.convertToDTO(actividadRepository.save(actividadDTOConverter.convertToEntity(actividadDTO))), HttpStatus.OK);
        }
        }

    @Override
    public ResponseEntity<?> consultarActividad(Integer id) {

        Optional<Actividad> actividadOptional = actividadRepository.findById(id);

        if (actividadOptional.isPresent()) {
            return ResponseEntity.ok(actividadDTOConverter.convertToDTO( actividadOptional.get()));
        } else {
            String mensaje = "La actividad con ID " + id + " no fue encontrada.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }
    }

    @Override
    public ResponseEntity<ActividadDTO> actualizarActividad(ActividadDTO actividadDTO) {

        ResponseEntity<?> consulta = consultarActividad(actividadDTO.getIdActividad());

        if (consulta.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(actividadDTOConverter.convertToDTO(actividadRepository.save(actividadDTOConverter.convertToEntity(actividadDTO))), HttpStatus.OK);
        }
        return ResponseEntity.status(consulta.getStatusCode()).build();
    }

    @Override
    public ResponseEntity<?> consultarActividad() {
        List<Actividad> actividades  = actividadRepository.findAll();

        if (!actividades.isEmpty()) {
            List<ActividadDTO> actividadesDTO = actividades.stream()
                    .map(actividadDTOConverter::convertToDTO)
                    .toList();

            return ResponseEntity.ok(actividadesDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existen registros.");
        }
    }
}
