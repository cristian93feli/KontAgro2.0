package com.kontagro.controllers;

import com.kontagro.dto.Class.ErrorDTO;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ConflictException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDTO> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDTO> handleConflictException(ConflictException ex, WebRequest request) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDTO> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return respuesta(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            WebRequest request
    ) {
        LOGGER.warn("Solicitud con formato inválido en {}", obtenerPath(request), ex);
        return respuesta(
                HttpStatus.BAD_REQUEST,
                "La información enviada no tiene un formato válido. Verifique los campos e intente nuevamente.",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request
    ) {
        LOGGER.warn("Violación de integridad de datos en {}", obtenerPath(request), ex);
        return respuesta(
                HttpStatus.CONFLICT,
                "No fue posible guardar la información porque existe una relación o restricción de datos que lo impide.",
                request
        );
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ErrorDTO> handleInvalidDataAccessResourceUsageException(
            InvalidDataAccessResourceUsageException ex,
            WebRequest request
    ) {
        LOGGER.error("La estructura de base de datos no coincide con el modelo actual en {}", obtenerPath(request), ex);
        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "La estructura actual de la base de datos no coincide con el modelo de KontAgro. Reinicie el backend para que Hibernate aplique las actualizaciones de desarrollo y revise el log del servidor si el problema continúa.",
                request
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorDTO> handleDataAccessException(DataAccessException ex, WebRequest request) {
        LOGGER.error("Error de acceso a datos en {}", obtenerPath(request), ex);
        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No fue posible acceder a la información solicitada en este momento. Intente nuevamente y, si el problema continúa, revise la conexión con la base de datos.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex, WebRequest request) {
        LOGGER.error("Error no controlado en {}", obtenerPath(request), ex);
        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No fue posible completar la operación. El detalle técnico quedó registrado en el servidor para su revisión.",
                request
        );
    }

    private ResponseEntity<ErrorDTO> respuesta(HttpStatus status, String mensaje, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                obtenerPath(request)
        );
        return new ResponseEntity<>(errorDTO, status);
    }

    private String obtenerPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
