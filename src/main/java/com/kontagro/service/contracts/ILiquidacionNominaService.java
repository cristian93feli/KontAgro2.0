package com.kontagro.service.contracts;

import com.kontagro.dto.LiquidacionNominaDTO;
import com.kontagro.dto.LiquidarNominaRequestDTO;
import com.kontagro.dto.RegistrarPagoTrabajadorDTO;

import java.util.List;

public interface ILiquidacionNominaService {
    LiquidacionNominaDTO liquidarNomina(LiquidarNominaRequestDTO request);
    LiquidacionNominaDTO consultarLiquidacion(Integer id);
    List<LiquidacionNominaDTO> listarLiquidaciones();
    List<RegistrarPagoTrabajadorDTO> listarPagosLiquidacion(Integer idLiquidacion);
    LiquidacionNominaDTO anularLiquidacion(Integer id);
}
