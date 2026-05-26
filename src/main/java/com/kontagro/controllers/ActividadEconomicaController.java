package com.kontagro.controllers;

import com.kontagro.dto.Class.ActividadEconomicaDTO;
import com.kontagro.service.contracts.IActividadEconomicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/actividades-economicas")
@RequiredArgsConstructor
public class ActividadEconomicaController {

    private final IActividadEconomicaService service;

    @PostMapping
    public ResponseEntity<ActividadEconomicaDTO> crear(@RequestBody ActividadEconomicaDTO dto) {
        return new ResponseEntity<>(service.crearActividadEconomica(dto), HttpStatus.CREATED);
    }

    @GetMapping("/id")
    public ResponseEntity<ActividadEconomicaDTO> consultarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.consultarActividadEconomica(id));
    }

    @PutMapping
    public ResponseEntity<ActividadEconomicaDTO> actualizar(@RequestBody ActividadEconomicaDTO dto) {
        return ResponseEntity.ok(service.actualizarActividadEconomica(dto));
    }

    @GetMapping
    public ResponseEntity<List<ActividadEconomicaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarActividadesEconomicas());
    }
}