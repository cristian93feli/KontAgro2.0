package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.entities.Actividad;
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
        IngresoDTO dto = modelMapper.map(ingreso, IngresoDTO.class);
        if (ingreso.getActividad() != null) {
            dto.setIdActividad(ingreso.getActividad().getIdActividad());
            dto.setNombreActividad(ingreso.getActividad().getNombreActividad());
        }
        return dto;
    }

    public Ingreso convertToEntity(IngresoDTO ingresoDTO) {
        Ingreso ingreso = modelMapper.map(ingresoDTO, Ingreso.class);
        if (ingresoDTO.getIdActividad() != null) {
            Actividad actividad = new Actividad();
            actividad.setIdActividad(ingresoDTO.getIdActividad());
            ingreso.setActividad(actividad);
        }
        return ingreso;
    }

    public List<IngresoDTO> convertToDTOList(List<Ingreso> ingresos) {
        return ingresos.stream().map(this::convertToDTO).toList();
    }
}
