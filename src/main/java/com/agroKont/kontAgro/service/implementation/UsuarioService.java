package com.agroKont.kontAgro.service.implementation;

import com.agroKont.kontAgro.entities.Usuario;
import com.agroKont.kontAgro.repository.IUsuarioRepository;
import com.agroKont.kontAgro.service.contracts.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public ResponseEntity<Usuario> crearUsuario(Usuario usuario) {
        return new ResponseEntity<>(usuarioRepository.save(usuario), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> consultarUsuario(Long id) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.get());
        } else {
            String mensaje = "El usuario con ID " + id + " no fue encontrado.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }
    }

    @Override
    public ResponseEntity<Usuario> actualizarUsuario(Usuario usuario) {

        ResponseEntity<?> consulta = consultarUsuario(usuario.getId());

        if (consulta.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(usuarioRepository.save(usuario), HttpStatus.OK);
        }

        return ResponseEntity.status(consulta.getStatusCode()).build();
    }

    public Usuario login(String usuario, String contrasena) {
        return usuarioRepository.findByUsuarioAndContrasena(usuario, contrasena);
    }
}
