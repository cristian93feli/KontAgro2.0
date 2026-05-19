package com.kontagro.controllers;

import com.kontagro.dto.LiquidacionNominaDTO;
import com.kontagro.service.contracts.ILiquidacionNominaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/liquidaciones")
@RequiredArgsConstructor
public class LiquidacionNominaController {

    private final ILiquidacionNominaService liquidacionNominaService;

    @PostMapping
    public ResponseEntity<LiquidacionNominaDTO> crear(@RequestBody LiquidacionNominaDTO dto) {
        return new ResponseEntity<>(liquidacionNominaService.crearLiquidacion(dto), HttpStatus.CREATED);
    }

    @GetMapping("/id")
    public ResponseEntity<LiquidacionNominaDTO> consultarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(liquidacionNominaService.consultarLiquidacion(id));
    }

    @PutMapping
    public ResponseEntity<LiquidacionNominaDTO> actualizar(@RequestBody LiquidacionNominaDTO dto) {
        return ResponseEntity.ok(liquidacionNominaService.actualizarLiquidacion(dto));
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionNominaDTO>> listarTodas() {
        return ResponseEntity.ok(liquidacionNominaService.listarLiquidaciones());
    }
}