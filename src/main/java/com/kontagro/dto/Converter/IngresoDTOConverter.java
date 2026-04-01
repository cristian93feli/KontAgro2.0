package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.entities.Ingreso;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IngresoDTOConverter {


    private final ModelMapper modelMapper;

    public IngresoDTO convertToDTO(Ingreso ingreso) {
        return modelMapper.map(ingreso, IngresoDTO.class);
    }

    public Ingreso convertToEntity(IngresoDTO ingresoDTO) {
        return modelMapper.map(ingresoDTO, Ingreso.class);
    }

    public List<IngresoDTO> convertToDTOList(List<Ingreso> ingresos) {
        return ingresos.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
