package com.kontagro.exceptions;

/**
 * Representa conflictos de negocio en los que la solicitud es válida,
 * pero no puede aplicarse por el estado actual de la información.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
