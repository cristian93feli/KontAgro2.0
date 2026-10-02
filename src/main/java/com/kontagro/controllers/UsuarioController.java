package com.kontagro.controllers;

import com.kontagro.dto.Class.AuthResponseDTO;
import com.kontagro.dto.Class.UsuarioDTO;
import com.kontagro.service.contracts.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @Value("${application.security.refresh-token.cookie-name}")
    private String refreshCookieName;

    @Value("${application.security.refresh-token.cookie-secure}")
    private boolean refreshCookieSecure;

    @Value("${application.security.refresh-token.absolute-expiration}")
    private long refreshAbsoluteExpiration;

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
        AuthResponseDTO respuesta = usuarioService.login(usuarioDTO.getUsuario(), usuarioDTO.getContrasena());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, crearCookie(respuesta.getRefreshToken(), Duration.ofMillis(refreshAbsoluteExpiration)).toString())
                .body(respuesta);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refrescar(
            @CookieValue(name = "${application.security.refresh-token.cookie-name}", required = false) String refreshToken
    ) {
        return ResponseEntity.ok(usuarioService.refrescarSesion(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "${application.security.refresh-token.cookie-name}", required = false) String refreshToken
    ) {
        usuarioService.cerrarSesion(refreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, crearCookie("", Duration.ZERO).toString())
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarUsuario(@RequestParam Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie crearCookie(String valor, Duration maxAge) {
        return ResponseCookie.from(refreshCookieName, valor == null ? "" : valor)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSecure ? "None" : "Lax")
                .path("/api/usuario")
                .maxAge(maxAge)
                .build();
    }
}
