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

    private final ITareasRealizadasService tareasRealizadasService;

    @PostMapping
    public ResponseEntity<TareasRealizadasDTO> crear(@RequestBody TareasRealizadasDTO dto) {
        return new ResponseEntity<>(tareasRealizadasService.crearTareaRealizada(dto), HttpStatus.CREATED);
    }

    @GetMapping("/id")
    public ResponseEntity<TareasRealizadasDTO> consultarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(tareasRealizadasService.consultarTareaRealizada(id));
    }

    @PutMapping
    public ResponseEntity<TareasRealizadasDTO> actualizar(@RequestBody TareasRealizadasDTO dto) {
        return ResponseEntity.ok(tareasRealizadasService.actualizarTareaRealizada(dto));
    }

    @GetMapping
    public ResponseEntity<List<TareasRealizadasDTO>> listarTodas() {
        return ResponseEntity.ok(tareasRealizadasService.listarTareasRealizadas());
    }
}