package com.kontagro.controllers;

import com.kontagro.dto.Class.ErrorDTO;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice // Indica que esta clase manejará excepciones globalmente
public class GlobalExceptionHandler {

    // Maneja ResourceNotFoundException (Retorna 404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "") // Limpia la URI
        );
        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    // Maneja BadRequestException (Retorna 400 Bad Request)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDTO> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    // Maneja excepciones genéricas (Cualquier RuntimeException no capturada antes)
    // Retorna 500 Internal Server Error (No exponer detalles técnicos en producción)
    @ExceptionHandler(Exception.class) // O RuntimeException.class para ser más preciso
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex, WebRequest request) {
        // En producción, solo muestra un mensaje genérico. Registra (log) el detalle técnico.
        String message = "Ocurrió un error interno inesperado.";
        // if (environment.acceptsProfiles(Profiles.of("dev", "test"))) { // Solo para entorno dev
        //     message = ex.getMessage();
        // }

        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}