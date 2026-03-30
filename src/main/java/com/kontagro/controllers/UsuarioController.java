package com.kontagro.controllers;

import com.kontagro.dto.Class.UsuarioDTO;
import com.kontagro.entities.Usuario;
import com.kontagro.service.contracts.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {


    private final IUsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.crearUsuario(usuarioDTO);
    }

    @GetMapping
    public ResponseEntity<?> consultarUsuario(@RequestParam Long id) {
        return usuarioService.consultarUsuario(id);
    }

    @PutMapping
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.actualizarUsuario(usuarioDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDTO usuarioDTO) {

        Usuario usuarioEncontrado = usuarioService.login(usuarioDTO.(), usuarioDTO.getContrasena());

        if (usuarioEncontrado != null) {
            return ResponseEntity.ok(usuarioEncontrado);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }
    }
}
