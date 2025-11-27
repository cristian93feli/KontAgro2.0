package com.agroKont.kontAgro.repository;

import com.agroKont.kontAgro.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsuarioAndContrasena(String usuario, String contraseñn);
}
