package com.agroKont.kontAgro.controllers;

import com.agroKont.kontAgro.dto.Class.ActividadDTO;
import com.agroKont.kontAgro.entities.Actividad;
import com.agroKont.kontAgro.service.contracts.IActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actividad")
@RequiredArgsConstructor
public class ActividadController {


    private final IActividadService actividadService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearActividad(@RequestBody ActividadDTO actividadDTO){
        return actividadService.crearActividad(actividadDTO);
    }

    @GetMapping
    public ResponseEntity<?> consultarActividad(@RequestParam Integer id){
        return actividadService.consultarActividad(id);
    }

    @PutMapping
    public ResponseEntity<ActividadDTO> actualizarActividad(@RequestBody ActividadDTO actividadDTO){
        return actividadService.actualizarActividad(actividadDTO);
    }

    @GetMapping("/actividades")
    public ResponseEntity<?> consultarActividad(){
        return actividadService.consultarActividad();
    }
}
