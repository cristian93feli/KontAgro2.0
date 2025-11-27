package com.agroKont.kontAgro.controllers;

import com.agroKont.kontAgro.entities.Actividad;
import com.agroKont.kontAgro.service.contracts.IActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actividad")
public class ActividadController {

    @Autowired
    private IActividadService actividadService;

    @PostMapping("/crear")
    public ResponseEntity<Actividad> crearActividad(@RequestBody Actividad actividad){
        return actividadService.crearActividad(actividad);
    }

    @GetMapping
    public ResponseEntity<?> consultarActividad(@RequestParam Integer id){
        return actividadService.consultarActividad(id);
    }

    @PutMapping
    public ResponseEntity<Actividad> actualizarActividad(@RequestBody Actividad actividad){
        return actividadService.actualizarActividad(actividad);
    }

    @GetMapping("/actividades")
    public ResponseEntity<?> consultarActividad(){
        return actividadService.consultarActividad();
    }
}
