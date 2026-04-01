package com.kontagro.service.contracts;

import com.kontagro.dto.Class.IngresoDTO;;
import org.apache.coyote.BadRequestException;

import java.time.LocalDate;
import java.util.List;


public interface IIngresoService {

    IngresoDTO crearIngreso(IngresoDTO ingresoDTO);

    IngresoDTO consultarIngreso(Integer id);

    IngresoDTO actualizarIngreso(IngresoDTO ingresoDTO);

    List<IngresoDTO> consultarIngreso();

    List<IngresoDTO> consultarIngresoPorFecha(LocalDate fechaInicial,LocalDate fechaFinal)
            throws BadRequestException;

    void eliminarIngreso(Integer id);
}

