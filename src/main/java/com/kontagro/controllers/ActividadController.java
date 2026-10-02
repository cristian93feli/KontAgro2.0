package com.kontagro.controllers;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.dto.TipoMovimientoOpcionDTO;
import com.kontagro.entities.enums.TipoMovimiento;
import com.kontagro.service.contracts.IActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actividad")
@RequiredArgsConstructor
public class ActividadController {

    private final IActividadService actividadService;

    @PostMapping("/crear")
    public ResponseEntity<ActividadDTO> crearActividad(@RequestBody ActividadDTO actividadDTO) {
        return ResponseEntity.ok(actividadService.crearActividad(actividadDTO));
    }

    @GetMapping
    public ResponseEntity<ActividadDTO> consultarActividad(@RequestParam Integer id) {
        return ResponseEntity.ok(actividadService.consultarActividad(id));
    }

    @PutMapping
    public ResponseEntity<ActividadDTO> actualizarActividad(@RequestBody ActividadDTO actividadDTO) {
        return ResponseEntity.ok(actividadService.actualizarActividad(actividadDTO));
    }

    @GetMapping("/actividades")
    public ResponseEntity<Page<ActividadDTO>> consultarActividad(Pageable pageable) {
        return ResponseEntity.ok(actividadService.consultarActividad(pageable));
    }

    @GetMapping("/combo")
    public ResponseEntity<List<ActividadDTO>> listarCombo(
            @RequestParam(required = false) TipoMovimiento tipoMovimiento
    ) {
        return ResponseEntity.ok(actividadService.listarCombo(tipoMovimiento));
    }

    @GetMapping("/tipos-movimiento")
    public ResponseEntity<List<TipoMovimientoOpcionDTO>> listarTiposMovimiento() {
        return ResponseEntity.ok(actividadService.listarTiposMovimiento());
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarActividad(@RequestParam Integer id) {
        actividadService.eliminarActividad(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/actividadEconomica")
    public ResponseEntity<List<ActividadDTO>> listarPorActividadEconomica(
            @RequestParam Integer idActividadEconomica
    ) {
        return ResponseEntity.ok(
                actividadService.listarActividadesPorActividadEconomica(idActividadEconomica)
        );
    }
}
