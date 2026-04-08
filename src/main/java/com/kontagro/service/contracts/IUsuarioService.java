package com.kontagro.service.contracts;

import com.kontagro.dto.Class.AuthResponseDTO;
import com.kontagro.dto.Class.UsuarioDTO;

public interface IUsuarioService {

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO consultarUsuario(Long id);

    UsuarioDTO actualizarUsuario(UsuarioDTO usuarioDTO);

    AuthResponseDTO login(String usuario, String contraseña);

    void eliminarUsuario(Long id);
}
