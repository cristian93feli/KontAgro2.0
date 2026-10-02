package com.kontagro.service.contracts;

import com.kontagro.dto.ContabilidadResumenDTO;

import java.time.LocalDate;

public interface IContabilidadService {
    ContabilidadResumenDTO consultarResumen(LocalDate fechaInicial, LocalDate fechaFinal);
}
