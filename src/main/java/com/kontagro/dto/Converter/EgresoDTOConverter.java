package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.entities.Egreso;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class EgresoDTOConverter {

    private final ModelMapper modelMapper;

    public EgresoDTO convertToDTO(Egreso egreso) {
        return modelMapper.map(egreso, EgresoDTO.class);
    }

    public Egreso convertToEntity(EgresoDTO egresoDTO) {
        return modelMapper.map(egresoDTO, Egreso.class);
    }

    public List<EgresoDTO> convertToDTOList(List<Egreso> egresos) {
       return egresos.stream()
                .map(this::convertToDTO)
                .toList();
    }
}