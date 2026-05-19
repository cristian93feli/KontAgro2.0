package com.kontagro.service.contracts;

import com.kontagro.dto.TareasRealizadasDTO;
import java.util.List;

public interface ITareasRealizadasService {
    TareasRealizadasDTO crearTareaRealizada(TareasRealizadasDTO dto);
    TareasRealizadasDTO consultarTareaRealizada(Integer id);
    TareasRealizadasDTO actualizarTareaRealizada(TareasRealizadasDTO dto);
    List<TareasRealizadasDTO> listarTareasRealizadas();
}