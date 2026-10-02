package com.kontagro.controllers;

import com.kontagro.dto.TareasRealizadasDTO;
import com.kontagro.service.contracts.ITareasRealizadasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tareas-realizadas")
@RequiredArgsConstructor
public class TareasRealizadasController {

    private final ITareasRealizadasService service;

    @PostMapping
    public ResponseEntity<TareasRealizadasDTO> crear(@RequestBody TareasRealizadasDTO dto) {
        return new ResponseEntity<>(service.crearTareaRealizada(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareasRealizadasDTO> consultarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.consultarTareaRealizada(id));
    }

    @PutMapping
    public ResponseEntity<TareasRealizadasDTO> actualizar(@RequestBody TareasRealizadasDTO dto) {
        return ResponseEntity.ok(service.actualizarTareaRealizada(dto));
    }

    @GetMapping
    public ResponseEntity<List<TareasRealizadasDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTareasRealizadas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<TareasRealizadasDTO>> listarActivas() {
        return ResponseEntity.ok(service.listarTareasActivas());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TareasRealizadasDTO> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam boolean activo
    ) {
        return ResponseEntity.ok(service.cambiarEstado(id, activo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminarTareaRealizada(id);
        return ResponseEntity.noContent().build();
    }
}
