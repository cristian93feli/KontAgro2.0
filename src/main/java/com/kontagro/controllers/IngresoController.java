package com.kontagro.controllers;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.service.contracts.IIngresoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
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
        return new ResponseEntity<>(ingresoService.crearIngreso(ingresoDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<IngresoDTO> consultarIngreso(@RequestParam Integer id) {
        return new ResponseEntity<>(ingresoService.consultarIngreso(id), HttpStatus.OK);
    }

    @GetMapping("/ingresos")
    public ResponseEntity<List<IngresoDTO>> consultarIngreso() {
        return new ResponseEntity<>(ingresoService.consultarIngreso(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<IngresoDTO> actualizarIngreso(@RequestBody IngresoDTO ingresoDTO) {
        return new ResponseEntity<>(ingresoService.actualizarIngreso(ingresoDTO), HttpStatus.OK);
    }

    @GetMapping("/consultarFechas")
    public ResponseEntity<List<IngresoDTO>> consultarIngresosPorFechas(@RequestParam LocalDate fechaInicial,
                                                        @RequestParam LocalDate fechaFinal)
                                                        throws BadRequestException {
        List<IngresoDTO> respuesta =
                ingresoService.consultarIngresoPorFecha(fechaInicial, fechaFinal);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping
    public ResponseEntity <Void>  eliminarIngreso(@RequestParam Integer id){
        ingresoService.eliminarIngreso(id);
        return ResponseEntity.noContent().build();
    }
}

