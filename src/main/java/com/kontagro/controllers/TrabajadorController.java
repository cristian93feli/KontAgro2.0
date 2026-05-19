package com.kontagro.controllers;

import com.kontagro.dto.TrabajadorDTO;
import com.kontagro.service.contracts.ITrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/trabajadores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrabajadorController {

    private final ITrabajadorService trabajadorService;

    @PostMapping
    public ResponseEntity<TrabajadorDTO> crear(@RequestBody TrabajadorDTO dto) {
        return new ResponseEntity<>(trabajadorService.crearTrabajador(dto), HttpStatus.CREATED);
    }

    @GetMapping("/id")
    public ResponseEntity<TrabajadorDTO> consultarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(trabajadorService.consultarTrabajador(id));
    }

    @PutMapping
    public ResponseEntity<TrabajadorDTO> actualizar(@RequestBody TrabajadorDTO dto) {
        return ResponseEntity.ok(trabajadorService.actualizarTrabajador(dto));
    }

    @GetMapping
    public ResponseEntity<List<TrabajadorDTO>> listarTodos() {
        return ResponseEntity.ok(trabajadorService.listarTrabajadores());
    }
}