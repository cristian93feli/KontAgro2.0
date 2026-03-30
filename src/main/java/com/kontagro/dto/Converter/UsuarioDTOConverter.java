package com.kontagro.dto.Converter;

import com.kontagro.dto.Class.UsuarioDTO;
import com.kontagro.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioDTOConverter {

    private final ModelMapper modelMapper;

    public UsuarioDTO convertToDTO(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    public Usuario convertToEntity(UsuarioDTO usuarioDTO) {
        return modelMapper.map(usuarioDTO, Usuario.class);
    }
}
