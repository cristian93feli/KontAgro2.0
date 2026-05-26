package com.kontagro.dto.Converter;


import com.kontagro.dto.Class.ActividadEconomicaDTO;
import com.kontagro.entities.ActividadEconomica;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ActividadEconomicaDTOConverter {

    private final ModelMapper modelMapper;

    public ActividadEconomicaDTO convertToDTO(ActividadEconomica actividadEconomica) {
        return modelMapper.map(actividadEconomica, ActividadEconomicaDTO.class);
    }

    public ActividadEconomica convertToEntity(ActividadEconomicaDTO dto) {
        return modelMapper.map(dto, ActividadEconomica.class);
    }

    public List<ActividadEconomicaDTO> convertToDTOList(List<ActividadEconomica> lista) {
        return lista.stream()
                .map(this::convertToDTO)
                .toList();
    }
}