package com.kontagro.dto.Converter;

import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
import com.kontagro.entities.RegistrarPagoTrabajador;
import com.kontagro.entities.enums.EstadoPago;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RegistrarPagoTrabajadorDTOConverter {

    public RegistrarPagoTrabajadorDTO convertToDTO(RegistrarPagoTrabajador entidad) {
        RegistrarPagoTrabajadorDTO dto = new RegistrarPagoTrabajadorDTO();
        dto.setId(entidad.getId());
        dto.setIdTrabajador(entidad.getTrabajador() != null ? entidad.getTrabajador().getId() : null);
        dto.setIdTarea(entidad.getTarea() != null ? entidad.getTarea().getId() : null);
        dto.setNombreTarea(entidad.getTarea() != null ? entidad.getTarea().getNombre() : null);
        dto.setFechaInicial(entidad.getFechaInicial());
        dto.setFechaFinal(entidad.getFechaFinal());
        dto.setDiasTrabajados(obtenerDiasTrabajados(entidad));
        dto.setValorPagar(entidad.getValorPagar());
        dto.setValorDescuentos(entidad.getValorDescuentos() == null ? BigDecimal.ZERO : entidad.getValorDescuentos());
        dto.setObservaciones(entidad.getObservaciones() == null || entidad.getObservaciones().isBlank() ? null : entidad.getObservaciones());
        dto.setEstado(entidad.isPagado() ? EstadoPago.PAGADO : EstadoPago.PENDIENTE);
        return dto;
    }

    private List<LocalDate> obtenerDiasTrabajados(RegistrarPagoTrabajador entidad) {
        if (entidad.getDiasTrabajados() != null && !entidad.getDiasTrabajados().isEmpty()) {
            return entidad.getDiasTrabajados().stream().map(com.kontagro.entities.PagoDiaTrabajado::getFecha).toList();
        }
        List<LocalDate> historicos = new ArrayList<>();
        if (entidad.getFechaInicial() != null) historicos.add(entidad.getFechaInicial());
        if (entidad.getFechaFinal() != null && !entidad.getFechaFinal().equals(entidad.getFechaInicial())) historicos.add(entidad.getFechaFinal());
        return historicos;
    }

    public List<RegistrarPagoTrabajadorDTO> convertToDTOList(List<RegistrarPagoTrabajador> lista) {
        return lista.stream().map(this::convertToDTO).toList();
    }
}
