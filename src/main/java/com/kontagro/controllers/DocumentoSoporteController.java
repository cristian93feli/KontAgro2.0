package com.kontagro.controllers;

import com.kontagro.dto.Class.DocumentoSoporteDTO;
import com.kontagro.entities.enums.TipoDocumentoSoporte;
import com.kontagro.service.contracts.IDocumentoSoporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/documentos-soporte")
@RequiredArgsConstructor
public class DocumentoSoporteController {

    private final IDocumentoSoporteService documentoService;

    @PostMapping(value = "/ingreso/{idIngreso}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoSoporteDTO> agregarAIngreso(
            @PathVariable Integer idIngreso,
            @RequestParam TipoDocumentoSoporte tipoDocumento,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDocumento,
            @RequestParam(required = false) String nombreTercero,
            @RequestParam(required = false) String identificacionTercero,
            @RequestPart("archivo") MultipartFile archivo
    ) {
        return ResponseEntity.ok(documentoService.agregarAIngreso(idIngreso, tipoDocumento, numeroDocumento,
                fechaDocumento, nombreTercero, identificacionTercero, archivo));
    }

    @PostMapping(value = "/egreso/{idEgreso}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoSoporteDTO> agregarAEgreso(
            @PathVariable Integer idEgreso,
            @RequestParam TipoDocumentoSoporte tipoDocumento,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDocumento,
            @RequestParam(required = false) String nombreTercero,
            @RequestParam(required = false) String identificacionTercero,
            @RequestPart("archivo") MultipartFile archivo
    ) {
        return ResponseEntity.ok(documentoService.agregarAEgreso(idEgreso, tipoDocumento, numeroDocumento,
                fechaDocumento, nombreTercero, identificacionTercero, archivo));
    }

    @GetMapping("/ingreso/{idIngreso}")
    public ResponseEntity<List<DocumentoSoporteDTO>> listarIngreso(@PathVariable Integer idIngreso) {
        return ResponseEntity.ok(documentoService.listarPorIngreso(idIngreso));
    }

    @GetMapping("/egreso/{idEgreso}")
    public ResponseEntity<List<DocumentoSoporteDTO>> listarEgreso(@PathVariable Integer idEgreso) {
        return ResponseEntity.ok(documentoService.listarPorEgreso(idEgreso));
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) {
        IDocumentoSoporteService.DocumentoDescarga documento = documentoService.obtenerParaDescarga(id);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(documento.tipoMime());
        } catch (Exception ex) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.nombreArchivo().replace("\"", "") + "\"")
                .contentType(mediaType)
                .body(documento.resource());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        documentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
