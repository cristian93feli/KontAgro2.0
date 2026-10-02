package com.kontagro.dto.Converter;

import com.kontagro.dto.TareasRealizadasDTO;
import com.kontagro.entities.TareasRealizadas;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TareasRealizadasDTOConverter {

    public TareasRealizadasDTO convertToDTO(TareasRealizadas entidad) {
        TareasRealizadasDTO dto = new TareasRealizadasDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setActivo(entidad.isActivo());
        return dto;
    }

    public List<TareasRealizadasDTO> convertToDTOList(List<TareasRealizadas> lista) {
        return lista.stream().map(this::convertToDTO).toList();
    }
}
