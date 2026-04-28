package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Class.IngresoporActividadDTO;
import com.kontagro.entities.Ingreso;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class IngresoPorActividadConverter {
    @Autowired
    private ModelMapper modelMapper;

    public IngresoporActividadDTO convertToDTO(Object object) {

        return modelMapper.map(object, IngresoporActividadDTO.class);
    }

   /* public List<IngresoporActividadDTO> convertToDTOList(List<Object> object) {
        return object.stream()
                .map(this::convertToDTO)
                .toList();
    }*/
}
