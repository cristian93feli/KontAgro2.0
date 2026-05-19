package com.kontagro.dto.Converter;

import com.kontagro.dto.TareasRealizadasDTO;
import com.kontagro.entities.TareasRealizadas;
import com.kontagro.entities.Actividad;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TareasRealizadasDTOConverter {

    private final ModelMapper modelMapper;

    public TareasRealizadasDTO convertToDTO(TareasRealizadas tareasRealizadas) {
        TareasRealizadasDTO dto = modelMapper.map(tareasRealizadas, TareasRealizadasDTO.class);
        if (tareasRealizadas.getActividad() != null) {
            dto.setIdActividad(tareasRealizadas.getActividad().getIdActividad()); // Se extrae el ID de Actividad
        }
        return dto;
    }

    public TareasRealizadas convertToEntity(TareasRealizadasDTO dto) {
        TareasRealizadas entidad = modelMapper.map(dto, TareasRealizadas.class);
        if (dto.getIdActividad() != null) {
            Actividad actividad = new Actividad();
            actividad.setIdActividad(dto.getIdActividad());
            entidad.setActividad(actividad);
        }
        return entidad;
    }

    public List<TareasRealizadasDTO> convertToDTOList(List<TareasRealizadas> lista) {
        return lista.stream()
                .map(this::convertToDTO)
                .toList();
    }
}