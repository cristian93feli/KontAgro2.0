package com.kontagro.controllers;

import com.kontagro.dto.Class.AuthResponseDTO;
import com.kontagro.dto.Class.UsuarioDTO;
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
        return new ResponseEntity<>(usuarioService.crearUsuario(usuarioDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> consultarUsuario(@RequestParam Long id) {
        return new ResponseEntity<>(usuarioService.consultarUsuario(id), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.actualizarUsuario(usuarioDTO), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.login(usuarioDTO.getUsuario(), usuarioDTO.getContrasena()), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarUsuario(@RequestParam Long id ){
         usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }
}
