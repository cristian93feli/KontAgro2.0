package com.kontagro.controllers;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.service.contracts.IIngresoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ingreso")
@RequiredArgsConstructor
public class IngresoController {

    private final IIngresoService ingresoService;

    @PostMapping
    public ResponseEntity<IngresoDTO> crearIngreso(@RequestBody IngresoDTO ingresoDTO) {
        return ingresoService.crearIngreso(ingresoDTO);
    }

    @GetMapping
    public ResponseEntity<?> consultarIngreso(@RequestParam Integer id) {
        return ingresoService.consultarIngreso(id);
    }

    @GetMapping("/ingresos")
    public ResponseEntity<?> consultarIngreso() {
        return ingresoService.consultarIngreso();
    }

    @PutMapping
    public ResponseEntity<IngresoDTO> actualizarIngreso(@RequestBody IngresoDTO ingresoDTO) {
        return ingresoService.actualizarIngreso(ingresoDTO);
    }

    @GetMapping("/consultarFechas")
    public ResponseEntity<?> consultarIngresosPorFechas(@RequestParam LocalDate fechaInicial,
                                                        @RequestParam LocalDate fechaFinal)
                                                        throws BadRequestException {
        List<IngresoDTO> respuesta =
                ingresoService.consultarIngresoPorFecha(fechaInicial, fechaFinal);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping
    public ResponseEntity <String>  eliminarIngreso(@RequestParam Integer id){
        return ingresoService.eliminarIngreso(id);
    }
}

