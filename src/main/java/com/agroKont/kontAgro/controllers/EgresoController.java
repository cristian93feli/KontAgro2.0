package com.agroKont.kontAgro.controllers;

import com.agroKont.kontAgro.entities.Egreso;
import com.agroKont.kontAgro.service.contracts.IEgresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/egreso")
public class EgresoController {

    @Autowired
    private IEgresoService egresoService;

    @PostMapping
    public ResponseEntity<Egreso> crearEgreso(@RequestBody Egreso egreso){
        return egresoService.crearEgreso(egreso);
    }

    @GetMapping
    public ResponseEntity<?> consultarEgreso(@RequestParam Integer id){
        return egresoService.consultarEgreso(id);
    }

    @PutMapping
    public ResponseEntity<Egreso> actualizarEgreso(@RequestBody Egreso egreso){
        return egresoService.actualizarEgreso(egreso);
    }

    @GetMapping("/egresos")
    public ResponseEntity<?> consultarEgreso(){
        return egresoService.consultarEgreso();
    }
}
