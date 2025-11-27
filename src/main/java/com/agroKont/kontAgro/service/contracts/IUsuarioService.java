package com.agroKont.kontAgro.service.contracts;

import com.agroKont.kontAgro.entities.Usuario;
import org.springframework.http.ResponseEntity;

public interface IUsuarioService {

    ResponseEntity<Usuario> crearUsuario(Usuario usuario);

    ResponseEntity<?> consultarUsuario(Long id);

    ResponseEntity<Usuario> actualizarUsuario(Usuario usuario);

    public Usuario login(String usuario, String contraseña);
}
