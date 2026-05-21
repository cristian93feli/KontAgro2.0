package com.kontagro.controllers;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Class.IngresoporActividadDTO;
import com.kontagro.reports.contracts.IReportGenerator;
import com.kontagro.service.contracts.IIngresoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ingreso")
@RequiredArgsConstructor
public class IngresoController {

    private final IIngresoService ingresoService;
    private final IReportGenerator<IngresoporActividadDTO> reportGenerator;

    @PostMapping
    public ResponseEntity<IngresoDTO> crearIngreso(@RequestBody IngresoDTO ingresoDTO) {
        return new ResponseEntity<>(ingresoService.crearIngreso(ingresoDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<IngresoDTO> consultarIngreso(@RequestParam Integer id) {
        return new ResponseEntity<>(ingresoService.consultarIngreso(id), HttpStatus.OK);
    }

    @GetMapping("/ingresos")
    public ResponseEntity<Page<IngresoDTO>> consultarIngreso(Pageable pageable) {

        return ResponseEntity.ok(
                ingresoService.consultarIngreso(pageable)
        );
    }

    @PutMapping
    public ResponseEntity<IngresoDTO> actualizarIngreso(@RequestBody IngresoDTO ingresoDTO) {
        return new ResponseEntity<>(ingresoService.actualizarIngreso(ingresoDTO), HttpStatus.OK);
    }

    @GetMapping("/consultarFechas")
    public ResponseEntity<List<IngresoporActividadDTO>> consultarIngresosPorFechas(@RequestParam LocalDate fechaInicial,
                                                        @RequestParam LocalDate fechaFinal)
                                                        throws BadRequestException {
        List<IngresoporActividadDTO> respuesta =
                ingresoService.consultarIngresoPorFecha(fechaInicial, fechaFinal);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping
    public ResponseEntity <Void>  eliminarIngreso(@RequestParam Integer id){
        ingresoService.eliminarIngreso(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reporteExcel")
    public ResponseEntity<Resource> exportarExcel(@RequestParam LocalDate fechaInicial,
                                                                                   @RequestParam LocalDate fechaFinal)
                                                                                    throws BadRequestException {
        List<IngresoporActividadDTO> lista =
                ingresoService.consultarIngresoPorFecha(fechaInicial, fechaFinal);
        byte[] archivo = reportGenerator.generateExcel(lista);
        ByteArrayResource recurso = new ByteArrayResource(archivo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingresos_" + fechaInicial + "_al_" + fechaFinal + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(archivo.length)
                .body(recurso);
    }
}

