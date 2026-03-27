package com.kontagro.repository;

import com.kontagro.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsuarioAndContrasena(String usuario, String contraseñn);
}
