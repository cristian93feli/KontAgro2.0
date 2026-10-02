package com.kontagro.service.implementation;

import com.kontagro.dto.Class.DocumentoSoporteDTO;
import com.kontagro.dto.ContabilidadResumenDTO;
import com.kontagro.reports.implementation.ContabilidadPdfReportGenerator;
import com.kontagro.service.contracts.IContabilidadExportService;
import com.kontagro.service.contracts.IContabilidadService;
import com.kontagro.service.contracts.IDocumentoSoporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ContabilidadExportServiceImpl implements IContabilidadExportService {

    private final IContabilidadService contabilidadService;
    private final ContabilidadPdfReportGenerator pdfReportGenerator;
    private final IDocumentoSoporteService documentoSoporteService;

    @Override
    public byte[] generarPaqueteContador(LocalDate fechaInicial, LocalDate fechaFinal) {
        ContabilidadResumenDTO resumen = contabilidadService.consultarResumen(fechaInicial, fechaFinal);
        byte[] pdf = pdfReportGenerator.generar(resumen);

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(salida)) {

            agregarBytes(zip,
                    "Informe_KontAgro_" + fechaInicial + "_al_" + fechaFinal + ".pdf",
                    pdf);

            Set<String> nombresUsados = new HashSet<>();
            if (resumen.getDocumentosSoporte() != null) {
                for (DocumentoSoporteDTO soporte : resumen.getDocumentosSoporte()) {
                    try {
                        IDocumentoSoporteService.DocumentoDescarga descarga = documentoSoporteService.obtenerParaDescarga(soporte.getId());
                        Resource resource = descarga.resource();
                        String carpeta = "INGRESO".equals(soporte.getOrigen()) ? "soportes/ingresos/" : "soportes/egresos/";
                        String nombre = nombreUnico(nombresUsados, carpeta + limpiarNombre(descarga.nombreArchivo()));
                        zip.putNextEntry(new ZipEntry(nombre));
                        try (var input = resource.getInputStream()) {
                            input.transferTo(zip);
                        }
                        zip.closeEntry();
                    } catch (Exception ignored) {
                        // Un soporte faltante no debe impedir generar el informe y los demás archivos disponibles.
                    }
                }
            }
            zip.finish();
            return salida.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible generar el paquete contable para el contador.", ex);
        }
    }

    private void agregarBytes(ZipOutputStream zip, String nombre, byte[] contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido);
        zip.closeEntry();
    }

    private String nombreUnico(Set<String> usados, String nombre) {
        if (usados.add(nombre)) return nombre;
        int punto = nombre.lastIndexOf('.');
        String base = punto > 0 ? nombre.substring(0, punto) : nombre;
        String extension = punto > 0 ? nombre.substring(punto) : "";
        int consecutivo = 2;
        String candidato;
        do {
            candidato = base + "_" + consecutivo++ + extension;
        } while (!usados.add(candidato));
        return candidato;
    }

    private String limpiarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return "documento";
        return nombre.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
