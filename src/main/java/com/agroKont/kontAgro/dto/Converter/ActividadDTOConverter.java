package com.agroKont.kontAgro.dto.Converter;

import com.agroKont.kontAgro.dto.Class.ActividadDTO;
import com.agroKont.kontAgro.entities.Actividad;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActividadDTOConverter {


    private final ModelMapper modelMapper;

    public ActividadDTO convertToDTO(Actividad actividad) {
        return modelMapper.map(actividad, ActividadDTO.class);
    }

    public Actividad convertToEntity(ActividadDTO actividadDTO) {
        return modelMapper.map(actividadDTO, Actividad.class);
    }
}
