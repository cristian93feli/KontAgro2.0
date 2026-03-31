package com.kontagro.exceptions;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
    // Útil para pasar la causa raíz
    //(Para errores técnicos, de base de datos, etc.):
    //
    //Opcionalmente, puedes crear una para errores que no son culpa del cliente.
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}