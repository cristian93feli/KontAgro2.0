package com.kontagro.service.contracts;


import com.kontagro.dto.Class.ActividadEconomicaDTO;

import java.util.List;

public interface IActividadEconomicaService {
    ActividadEconomicaDTO crearActividadEconomica(ActividadEconomicaDTO dto);
    ActividadEconomicaDTO consultarActividadEconomica(Integer id);
    ActividadEconomicaDTO actualizarActividadEconomica(ActividadEconomicaDTO dto);
    List<ActividadEconomicaDTO> listarActividadesEconomicas();
}