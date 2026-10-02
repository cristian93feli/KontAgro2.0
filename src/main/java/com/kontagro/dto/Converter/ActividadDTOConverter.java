package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.entities.Actividad;
import com.kontagro.entities.ActividadEconomica;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActividadDTOConverter {

    private final ModelMapper modelMapper;

    public ActividadDTO convertToDTO(Actividad actividad) {
        ActividadDTO dto = modelMapper.map(actividad, ActividadDTO.class);
        if (actividad.getActividadEconomica() != null) {
            dto.setIdActividadEconomica(actividad.getActividadEconomica().getId());
            dto.setNombreActividadEconomica(actividad.getActividadEconomica().getNombreActividadEconomica());
            dto.setTipoMovimiento(actividad.getActividadEconomica().getTipoMovimiento());
        }
        return dto;
    }

    public Actividad convertToEntity(ActividadDTO actividadDTO) {
        Actividad actividad = modelMapper.map(actividadDTO, Actividad.class);
        if (actividadDTO.getIdActividadEconomica() != null) {
            ActividadEconomica actividadEconomica = new ActividadEconomica();
            actividadEconomica.setId(actividadDTO.getIdActividadEconomica());
            actividad.setActividadEconomica(actividadEconomica);
        }
        return actividad;
    }

    public List<ActividadDTO> convertToDTOList(List<Actividad> actividades) {
        return actividades.stream().map(this::convertToDTO).toList();
    }
}
