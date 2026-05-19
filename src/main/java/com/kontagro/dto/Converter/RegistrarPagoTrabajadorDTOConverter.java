package com.kontagro.dto.Converter;

import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
import com.kontagro.entities.RegistrarPagoTrabajador;
import com.kontagro.entities.Trabajador;
import com.kontagro.entities.Actividad;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrarPagoTrabajadorDTOConverter {

    private final ModelMapper modelMapper;

    public RegistrarPagoTrabajadorDTO convertToDTO(RegistrarPagoTrabajador entidad) {
        RegistrarPagoTrabajadorDTO dto = modelMapper.map(entidad, RegistrarPagoTrabajadorDTO.class);

        if (entidad.getTrabajador() != null) {
            dto.setIdTrabajador(entidad.getTrabajador().getId()); // Asegúrate de que el ID en Trabajador sea 'id' o adáptalo
        }
        if (entidad.getActividad() != null) {
            dto.setIdActividad(entidad.getActividad().getIdActividad()); // Ajustado según tu ActividadDTO (idActividad)
        }

        return dto;
    }

    public RegistrarPagoTrabajador convertToEntity(RegistrarPagoTrabajadorDTO dto) {
        RegistrarPagoTrabajador entidad = modelMapper.map(dto, RegistrarPagoTrabajador.class);

        if (dto.getIdTrabajador() != null) {
            Trabajador trabajador = new Trabajador();
            trabajador.setId(dto.getIdTrabajador());
            entidad.setTrabajador(trabajador);
        }

        if (dto.getIdActividad() != null) {
            Actividad actividad = new Actividad();
            actividad.setIdActividad(dto.getIdActividad());
            entidad.setActividad(actividad);
        }

        return entidad;
    }

    public List<RegistrarPagoTrabajadorDTO> convertToDTOList(List<RegistrarPagoTrabajador> lista) {
        return lista.stream()
                .map(this::convertToDTO)
                .toList();
    }
}