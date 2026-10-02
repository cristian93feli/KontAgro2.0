package com.kontagro.dto.Converter;

import com.kontagro.dto.LiquidacionNominaDTO;
import com.kontagro.entities.LiquidacionNomina;
import com.kontagro.entities.enums.EstadoLiquidacion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LiquidacionNominaDTOConverter {

    public LiquidacionNominaDTO convertToDTO(LiquidacionNomina entidad) {
        LiquidacionNominaDTO dto = new LiquidacionNominaDTO();
        dto.setId(entidad.getId());
        dto.setIdTrabajador(entidad.getTrabajador() != null ? entidad.getTrabajador().getId() : null);
        dto.setFechaInicialPagado(entidad.getFechaInicialPagado());
        dto.setFechaFinalPagado(entidad.getFechaFinalPagado());
        dto.setValorTotalTrabajado(entidad.getValorTotalTrabajado());
        dto.setValorTotalDescuentos(entidad.getValorTotalDescuentos());
        dto.setValorTotalPagado(entidad.getValorTotalPagado());
        dto.setFechaLiquidacion(entidad.getFechaLiquidacion());
        dto.setFechaAnulacion(entidad.getFechaAnulacion());
        dto.setEstado(entidad.getEstado() == null ? EstadoLiquidacion.ACTIVA : entidad.getEstado());
        return dto;
    }

    public List<LiquidacionNominaDTO> convertToDTOList(List<LiquidacionNomina> lista) {
        return lista.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
