package com.kontagro.exceptions;

public class BadRequestException extends RuntimeException {
    //para errores de validación, formato, o lógica de negocio que no cumplen los requisitos al crear o actualizar.
    public BadRequestException(String message) {
        super(message);
    }
}