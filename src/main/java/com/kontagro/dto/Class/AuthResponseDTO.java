package com.kontagro.dto.Class;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UsuarioDTO usuario;
    private long tokenExpiresAt;

    @JsonIgnore
    private String refreshToken;
}
