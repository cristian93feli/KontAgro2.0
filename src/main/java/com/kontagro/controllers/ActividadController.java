package com.kontagro.controllers;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.service.contracts.IActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actividad")
@RequiredArgsConstructor
public class ActividadController {


    private final IActividadService actividadService;

    @PostMapping("/crear")
    public ResponseEntity<ActividadDTO> crearActividad(@RequestBody ActividadDTO actividadDTO){
        return ResponseEntity.ok(actividadService.crearActividad(actividadDTO));
    }

    @GetMapping
    public ResponseEntity<ActividadDTO> consultarActividad(@RequestParam Integer id){
        return ResponseEntity.ok(actividadService.consultarActividad(id));
    }

    @PutMapping
    public ResponseEntity<ActividadDTO> actualizarActividad(@RequestBody ActividadDTO actividadDTO){
        return ResponseEntity.ok(actividadService.actualizarActividad(actividadDTO));
    }

    @GetMapping("/actividades")
    public ResponseEntity<List<ActividadDTO>> consultarActividad(){
        return ResponseEntity.ok(actividadService.consultarActividad());
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarActividad(@RequestParam Integer id){
        actividadService.eliminarActividad(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/actividadEconomica")
    public ResponseEntity<List<ActividadDTO>> listarPorActividadEconomica(@RequestParam Integer idActividadEconomica) {
        List<ActividadDTO> actividades = actividadService.listarActividadesPorActividadEconomica(idActividadEconomica);
        return ResponseEntity.ok(actividades);
    }
}
