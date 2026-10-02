package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.entities.Actividad;
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
        EgresoDTO dto = modelMapper.map(egreso, EgresoDTO.class);
        if (egreso.getActividad() != null) {
            dto.setIdActividad(egreso.getActividad().getIdActividad());
            dto.setNombreActividad(egreso.getActividad().getNombreActividad());
        }
        return dto;
    }

    public Egreso convertToEntity(EgresoDTO egresoDTO) {
        Egreso egreso = modelMapper.map(egresoDTO, Egreso.class);
        if (egresoDTO.getIdActividad() != null) {
            Actividad actividad = new Actividad();
            actividad.setIdActividad(egresoDTO.getIdActividad());
            egreso.setActividad(actividad);
        }
        return egreso;
    }

    public List<EgresoDTO> convertToDTOList(List<Egreso> egresos) {
        return egresos.stream().map(this::convertToDTO).toList();
    }
}
