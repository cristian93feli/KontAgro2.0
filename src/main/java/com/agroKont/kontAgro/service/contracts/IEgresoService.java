package com.agroKont.kontAgro.service.contracts;

import com.agroKont.kontAgro.entities.Egreso;
import org.springframework.http.ResponseEntity;

public interface IEgresoService {

    ResponseEntity<Egreso> crearEgreso(Egreso egreso);

    ResponseEntity<?> consultarEgreso(Integer id);

    ResponseEntity<Egreso> actualizarEgreso(Egreso egreso);

    ResponseEntity<?> consultarEgreso();
}
