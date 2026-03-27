package com.kontagro.service.contracts;

import com.kontagro.entities.Usuario;
import org.springframework.http.ResponseEntity;

public interface IUsuarioService {

    ResponseEntity<Usuario> crearUsuario(Usuario usuario);

    ResponseEntity<?> consultarUsuario(Long id);

    ResponseEntity<Usuario> actualizarUsuario(Usuario usuario);

    public Usuario login(String usuario, String contraseña);
}
