package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.entities.Actividad;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<ActividadDTO> convertToDTOList(List<Actividad> actividades) {
        return actividades.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
