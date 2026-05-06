package com.kontagro.service.implementation;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.dto.Converter.ActividadDTOConverter;
import com.kontagro.entities.Actividad;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IActividadRepository;
import com.kontagro.service.contracts.IActividadService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActividadService implements IActividadService {

    private final IActividadRepository actividadRepository;
    private final ActividadDTOConverter actividadDTOConverter;

    @Override
    public ActividadDTO crearActividad(ActividadDTO actividadDTO) {
        //if (actividadRepository.existsById(actividadDTO.getIdActividad())) {
          //  throw new BadRequestException(String.format(MensajesError.ACTIVIDAD_YA_EXISTE, actividadDTO.getIdActividad()));
        //}

        Actividad entidad = actividadDTOConverter.convertToEntity(actividadDTO);
        Actividad guardada = actividadRepository.save(entidad);

        return actividadDTOConverter.convertToDTO(guardada);
    }

    @Override
    public ActividadDTO consultarActividad(Integer id) {

        Optional<Actividad> actividadOptional = actividadRepository.findById(id);

        if (actividadOptional.isPresent()) {
            return actividadDTOConverter.convertToDTO(actividadOptional.get());
        } else {
            throw new ResourceNotFoundException(String.format(MensajesError.ACTIVIDAD_NO_ENCONTRADA, id));
        }
    }

    @Override
    public ActividadDTO actualizarActividad(ActividadDTO actividadDTO) {

        ActividadDTO consulta = consultarActividad(actividadDTO.getIdActividad());

        if (consulta == null) {
            throw new ResourceNotFoundException(MensajesError.ACTIVIDAD_NO_EXISTE);
        }
        return actividadDTOConverter.convertToDTO
                (actividadRepository.save(actividadDTOConverter.convertToEntity(actividadDTO)));
    }

    @Override
    public List<ActividadDTO> consultarActividad() {
        List<Actividad> actividades = actividadRepository.findAll();

        if (!actividades.isEmpty()) {
            List<ActividadDTO> actividadesLista = actividadDTOConverter.convertToDTOList(actividades) ;

            return actividadesLista;
        } else {
             throw new BadRequestException(MensajesError.NO_EXISTE_REGISTROS);
        }
    }

    @Override
    public void eliminarActividad(Integer id) {
        if (!actividadRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format(MensajesError.ACTIVIDAD_NO_ENCONTRADA, id));
        }
        actividadRepository.deleteById(id);
    }
}
