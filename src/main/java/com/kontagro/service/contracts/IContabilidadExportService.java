package com.kontagro.service.contracts;

import java.time.LocalDate;

public interface IContabilidadExportService {
    byte[] generarPaqueteContador(LocalDate fechaInicial, LocalDate fechaFinal);
}
