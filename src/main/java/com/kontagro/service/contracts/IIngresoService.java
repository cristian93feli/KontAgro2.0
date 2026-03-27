package com.kontagro.service.contracts;

import com.kontagro.entities.Ingreso;
import org.springframework.http.ResponseEntity;

public interface IIngresoService {

    ResponseEntity<Ingreso> crearIngreso(Ingreso ingreso);

    ResponseEntity<?> consultarIngreso(Integer id);

    ResponseEntity<Ingreso> actualizarIngreso(Ingreso ingreso);

    ResponseEntity<?> consultarIngreso();
}
