package com.kontagro.controllers;

import com.kontagro.dto.ContabilidadResumenDTO;
import com.kontagro.reports.implementation.ContabilidadPdfReportGenerator;
import com.kontagro.service.contracts.IContabilidadService;
import com.kontagro.service.contracts.IContabilidadExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/contabilidad")
@RequiredArgsConstructor
public class ContabilidadController {

    private final IContabilidadService contabilidadService;
    private final ContabilidadPdfReportGenerator pdfReportGenerator;
    private final IContabilidadExportService contabilidadExportService;

    @GetMapping("/resumen")
    public ResponseEntity<ContabilidadResumenDTO> consultarResumen(
            @RequestParam LocalDate fechaInicial,
            @RequestParam LocalDate fechaFinal
    ) {
        return ResponseEntity.ok(contabilidadService.consultarResumen(fechaInicial, fechaFinal));
    }

    @GetMapping("/reporte-pdf")
    public ResponseEntity<Resource> generarReportePdf(
            @RequestParam LocalDate fechaInicial,
            @RequestParam LocalDate fechaFinal
    ) {
        ContabilidadResumenDTO resumen = contabilidadService.consultarResumen(fechaInicial, fechaFinal);
        byte[] pdf = pdfReportGenerator.generar(resumen);
        ByteArrayResource resource = new ByteArrayResource(pdf);
        String nombre = "kontagro_reporte_financiero_" + fechaInicial + "_al_" + fechaFinal + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(resource);
    }
    @GetMapping("/paquete-contador")
    public ResponseEntity<Resource> generarPaqueteContador(
            @RequestParam LocalDate fechaInicial,
            @RequestParam LocalDate fechaFinal
    ) {
        byte[] zip = contabilidadExportService.generarPaqueteContador(fechaInicial, fechaFinal);
        ByteArrayResource resource = new ByteArrayResource(zip);
        String nombre = "kontagro_contabilidad_" + fechaInicial + "_al_" + fechaFinal + ".zip";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre)
                .contentType(MediaType.parseMediaType("application/zip"))
                .contentLength(zip.length)
                .body(resource);
    }

}
