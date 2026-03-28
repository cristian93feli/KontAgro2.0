package com.kontagro.controllers;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.service.contracts.IIngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
