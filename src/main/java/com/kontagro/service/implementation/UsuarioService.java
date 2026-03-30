package com.kontagro.service.implementation;

import com.kontagro.dto.Class.UsuarioDTO;
import com.kontagro.dto.Converter.UsuarioDTOConverter;
import com.kontagro.entities.Usuario;
import com.kontagro.repository.IUsuarioRepository;
import com.kontagro.service.contracts.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final IUsuarioRepository iUsuarioRepository;
    private final UsuarioDTOConverter usuarioDTOConverter;


    @Override
    public ResponseEntity<UsuarioDTO> crearUsuario(UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioDTOConverter.convertToDTO
                (iUsuarioRepository.save(usuarioDTOConverter.convertToEntity(usuarioDTO))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> consultarUsuario(Long id) {

        Optional<Usuario> usuarioOptional = iUsuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.get());
        } else {
            String mensaje = "El usuario con ID " + id + " no fue encontrado.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }
    }

    @Override
    public ResponseEntity<UsuarioDTO> actualizarUsuario(UsuarioDTO usuarioDTO) {

        ResponseEntity<?> consulta = consultarUsuario(usuarioDTO.getId());

        if (consulta.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(usuarioDTOConverter.convertToDTO
                    (iUsuarioRepository.save(usuarioDTOConverter.convertToEntity(usuarioDTO))), HttpStatus.OK);
        }

        return ResponseEntity.status(consulta.getStatusCode()).build();
    }

    public UsuarioDTO login(String usuario, String contrasena) {
        return (usuarioDTOConverter.convertToDTO
                (iUsuarioRepository.findByUsuarioAndContrasena(usuario, contrasena)));
    }
}
