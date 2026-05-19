package com.kontagro.service.contracts;

import com.kontagro.dto.TrabajadorDTO;
import java.util.List;

public interface ITrabajadorService {
    TrabajadorDTO crearTrabajador(TrabajadorDTO dto);
    TrabajadorDTO consultarTrabajador(Integer id);
    TrabajadorDTO actualizarTrabajador(TrabajadorDTO dto);
    List<TrabajadorDTO> listarTrabajadores();
}