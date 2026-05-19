package com.kontagro.service.contracts;

import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
import java.util.List;

public interface IRegistrarPagoTrabajadorService {
    RegistrarPagoTrabajadorDTO crearPagoTrabajador(RegistrarPagoTrabajadorDTO dto);
    RegistrarPagoTrabajadorDTO consultarPagoTrabajador(Integer id);
    RegistrarPagoTrabajadorDTO actualizarPagoTrabajador(RegistrarPagoTrabajadorDTO dto);
    List<RegistrarPagoTrabajadorDTO> listarPagosTrabajadores();
}