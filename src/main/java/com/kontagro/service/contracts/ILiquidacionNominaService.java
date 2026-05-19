package com.kontagro.service.contracts;

import com.kontagro.dto.LiquidacionNominaDTO;
import java.util.List;

public interface ILiquidacionNominaService {
    LiquidacionNominaDTO crearLiquidacion(LiquidacionNominaDTO dto);
    LiquidacionNominaDTO consultarLiquidacion(Integer id);
    LiquidacionNominaDTO actualizarLiquidacion(LiquidacionNominaDTO dto);
    List<LiquidacionNominaDTO> listarLiquidaciones(); // Añadido para complementar el CRUD
}