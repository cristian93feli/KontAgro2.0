package com.kontagro.dto.Class;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDTO {
    private LocalDateTime timestamp;
    private int status; // Código de estado HTTP (ej. 404, 400)
    private String error;  // Nombre descriptivo (ej. "Not Found")
    private String message; // Mensaje detallado para el desarrollador/usuario
    private String path;    // La URL que provocó el error (opcional pero profesional)

    public ErrorDTO(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
