package com.kontagro.controllers;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.service.contracts.IEgresoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/egreso")
@RequiredArgsConstructor
public class EgresoController {


    private final IEgresoService egresoService;

    @PostMapping
    public ResponseEntity<EgresoDTO> crearEgreso(@RequestBody EgresoDTO egresoDTO) {
        return new ResponseEntity<>(egresoService.crearEgreso(egresoDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<EgresoDTO> consultarEgreso(@RequestParam Integer id) {
        return new ResponseEntity<>(egresoService.consultarEgreso(id), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<EgresoDTO> actualizarEgreso(@RequestBody EgresoDTO egresoDTO) {
        return new ResponseEntity<>(egresoService.actualizarEgreso(egresoDTO), HttpStatus.OK);
    }

    @GetMapping("/egresos")
    public ResponseEntity<Page<EgresoDTO>> consultarEgreso(Pageable pageable) {
        return new ResponseEntity<>(egresoService.consultarEgreso(pageable), HttpStatus.OK);

    }

    @GetMapping("/consultarFechas")
    public ResponseEntity<List<EgresoDTO>> consultarEgresoPorFecha(
            @RequestParam LocalDate fecha_inicial,
            @RequestParam LocalDate fecha_final) throws BadRequestException {

        List<EgresoDTO> respuesta =
                egresoService.consultarEgresoPorFecha(fecha_inicial, fecha_final);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarEgreso(@RequestParam Integer id){
        egresoService.eliminarEgreso(id);
        return ResponseEntity.noContent().build();
    }
}
