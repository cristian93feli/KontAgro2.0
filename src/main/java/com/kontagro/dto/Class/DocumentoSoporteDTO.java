package com.kontagro.dto.Class;

import com.kontagro.entities.enums.TipoDocumentoSoporte;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DocumentoSoporteDTO {
    private Long id;
    private String origen;
    private Integer idMovimiento;
    private TipoDocumentoSoporte tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaDocumento;
    private String nombreTercero;
    private String identificacionTercero;
    private String nombreArchivo;
    private String tipoMime;
    private long tamanoBytes;
    private LocalDateTime fechaCarga;
}
