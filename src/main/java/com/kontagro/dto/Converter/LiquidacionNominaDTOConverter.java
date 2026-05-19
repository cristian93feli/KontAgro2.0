package com.kontagro.dto.Converter;

import com.kontagro.dto.LiquidacionNominaDTO;
import com.kontagro.entities.LiquidacionNomina;
import com.kontagro.entities.Trabajador;
import com.kontagro.entities.Trabajador;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LiquidacionNominaDTOConverter {

    private final ModelMapper modelMapper;

    public LiquidacionNominaDTO convertToDTO(LiquidacionNomina liquidacionNomina) {
        LiquidacionNominaDTO dto = modelMapper.map(liquidacionNomina, LiquidacionNominaDTO.class);
        if (liquidacionNomina.getTrabajador() != null) {
            dto.setIdTrabajador(liquidacionNomina.getTrabajador().getId()); // Asumiendo que el ID del trabajador en su entidad es 'id'
        }
        return dto;
    }

    public LiquidacionNomina convertToEntity(LiquidacionNominaDTO dto) {
        LiquidacionNomina entidad = modelMapper.map(dto, LiquidacionNomina.class);
        if (dto.getIdTrabajador() != null) {
            Trabajador trabajador = new Trabajador();
            trabajador.setId(dto.getIdTrabajador()); // Asigna el ID recibido al objeto relacional
            entidad.setTrabajador(trabajador);
        }
        return entidad;
    }

    public List<LiquidacionNominaDTO> convertToDTOList(List<LiquidacionNomina> liquidaciones) {
        return liquidaciones.stream()
                .map(this::convertToDTO)
                .toList();
    }
}