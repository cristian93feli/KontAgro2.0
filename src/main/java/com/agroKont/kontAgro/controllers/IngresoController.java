package com.agroKont.kontAgro.controllers;

import com.agroKont.kontAgro.entities.Ingreso;
import com.agroKont.kontAgro.service.contracts.IIngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingreso")
@RequiredArgsConstructor
public class IngresoController {

    private final IIngresoService ingresoService;

    @PostMapping
    public ResponseEntity<Ingreso> crearIngreso(@RequestBody Ingreso ingreso){
        return ingresoService.crearIngreso(ingreso);
    }

    @GetMapping
    public ResponseEntity<?> consultarIngreso(@RequestParam Integer id){
        return ingresoService.consultarIngreso(id);
    }

    @GetMapping("/ingresos")
    public ResponseEntity<?> consultarIngreso(){
        return ingresoService.consultarIngreso();
    }

    @PutMapping
    public ResponseEntity<Ingreso> actualizarIngreso(@RequestBody Ingreso ingreso){
        return ingresoService.actualizarIngreso(ingreso);
    }
}
