package com.kontagro.controllers;

import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
import com.kontagro.service.contracts.IRegistrarPagoTrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrar-pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistrarPagoTrabajadorController {

    private final IRegistrarPagoTrabajadorService registrarPagoTrabajadorService;

    @PostMapping
    public ResponseEntity<RegistrarPagoTrabajadorDTO> crear(@RequestBody RegistrarPagoTrabajadorDTO dto) {
        return new ResponseEntity<>(registrarPagoTrabajadorService.crearPagoTrabajador(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrarPagoTrabajadorDTO> consultarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(registrarPagoTrabajadorService.consultarPagoTrabajador(id));
    }

    @PutMapping
    public ResponseEntity<RegistrarPagoTrabajadorDTO> actualizar(@RequestBody RegistrarPagoTrabajadorDTO dto) {
        return ResponseEntity.ok(registrarPagoTrabajadorService.actualizarPagoTrabajador(dto));
    }

    @GetMapping
    public ResponseEntity<List<RegistrarPagoTrabajadorDTO>> listarTodos() {
        return ResponseEntity.ok(registrarPagoTrabajadorService.listarPagosTrabajadores());
    }

    @GetMapping("/pendientes/trabajador/{idTrabajador}")
    public ResponseEntity<List<RegistrarPagoTrabajadorDTO>> listarPendientesPorTrabajador(
            @PathVariable Integer idTrabajador
    ) {
        return ResponseEntity.ok(registrarPagoTrabajadorService.listarPagosPendientesPorTrabajador(idTrabajador));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        registrarPagoTrabajadorService.eliminarPagoTrabajador(id);
        return ResponseEntity.noContent().build();
    }
}
