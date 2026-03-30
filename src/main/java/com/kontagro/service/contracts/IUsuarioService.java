package com.kontagro.service.contracts;

import com.kontagro.dto.Class.UsuarioDTO;
import org.springframework.http.ResponseEntity;

public interface IUsuarioService {

    ResponseEntity<UsuarioDTO> crearUsuario(UsuarioDTO usuarioDTO);

    ResponseEntity<?> consultarUsuario(Long id);

    ResponseEntity<UsuarioDTO> actualizarUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO login(String usuario, String contraseña);

    ResponseEntity<String> eliminarUsuario(Long id);
}
