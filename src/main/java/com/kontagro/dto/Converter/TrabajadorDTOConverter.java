package com.kontagro.dto.Converter;

import com.kontagro.dto.TrabajadorDTO;
import com.kontagro.entities.Trabajador;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrabajadorDTOConverter {

    private final ModelMapper modelMapper;

    public TrabajadorDTO convertToDTO(Trabajador trabajador) {
        return modelMapper.map(trabajador, TrabajadorDTO.class);
    }

    public Trabajador convertToEntity(TrabajadorDTO dto) {
        return modelMapper.map(dto, Trabajador.class);
    }

    public List<TrabajadorDTO> convertToDTOList(List<Trabajador> trabajadores) {
        return trabajadores.stream()
                .map(this::convertToDTO)
                .toList();
    }
}