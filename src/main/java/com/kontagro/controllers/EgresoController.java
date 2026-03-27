package com.kontagro.controllers;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.service.contracts.IEgresoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/egreso")
@RequiredArgsConstructor
public class EgresoController {


    private final IEgresoService egresoService;

    @PostMapping
    public ResponseEntity<EgresoDTO> crearEgreso(@RequestBody EgresoDTO egresoDTO) {
        return egresoService.crearEgreso(egresoDTO);
    }

    @GetMapping
    public ResponseEntity<?> consultarEgreso(@RequestParam Integer id) {
        return egresoService.consultarEgreso(id);
    }

    @PutMapping
    public ResponseEntity<EgresoDTO> actualizarEgreso(@RequestBody EgresoDTO egresoDTO) {
        return egresoService.actualizarEgreso(egresoDTO);
    }

    @GetMapping("/egresos")
    public ResponseEntity<?> consultarEgreso() {
        return egresoService.consultarEgreso();
    }

    @GetMapping("/consultarFechas")
    public ResponseEntity<?> consultarEgresoPorFecha(
            @RequestParam LocalDate fecha_inicial,
            @RequestParam LocalDate fecha_final) throws BadRequestException {
        return egresoService.consultarEgresoPorFecha(fecha_inicial, fecha_final);
    }
}
