package com.agroKont.kontAgro.service.contracts;

import com.agroKont.kontAgro.entities.Ingreso;
import org.springframework.http.ResponseEntity;

public interface IIngresoService {

    ResponseEntity<Ingreso> crearIngreso(Ingreso ingreso);

    ResponseEntity<?> consultarIngreso(Integer id);

    ResponseEntity<Ingreso> actualizarIngreso(Ingreso ingreso);

    ResponseEntity<?> consultarIngreso();
}
