package com.kontagro.controllers;

import com.kontagro.dto.LiquidacionNominaDTO;
import com.kontagro.dto.LiquidarNominaRequestDTO;
import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
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
    public ResponseEntity<LiquidacionNominaDTO> liquidar(@RequestBody LiquidarNominaRequestDTO request) {
        return new ResponseEntity<>(liquidacionNominaService.liquidarNomina(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionNominaDTO> consultarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(liquidacionNominaService.consultarLiquidacion(id));
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionNominaDTO>> listarTodas() {
        return ResponseEntity.ok(liquidacionNominaService.listarLiquidaciones());
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<List<RegistrarPagoTrabajadorDTO>> listarPagos(@PathVariable Integer id) {
        return ResponseEntity.ok(liquidacionNominaService.listarPagosLiquidacion(id));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<LiquidacionNominaDTO> anular(@PathVariable Integer id) {
        return ResponseEntity.ok(liquidacionNominaService.anularLiquidacion(id));
    }
}
