package com.kontagro.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    //cuando intentas buscar un dato unico y no lo encuentras arrojas esta excepcion
    public ResourceNotFoundException(String message) {
        super(message);
    }
}