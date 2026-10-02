package com.kontagro.service.contracts;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.dto.TipoMovimientoOpcionDTO;
import com.kontagro.entities.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IActividadService {

    ActividadDTO crearActividad(ActividadDTO actividad);

    ActividadDTO consultarActividad(Integer id);

    ActividadDTO actualizarActividad(ActividadDTO actividad);

    Page<ActividadDTO> consultarActividad(Pageable pageable);

    List<ActividadDTO> listarCombo();

    List<ActividadDTO> listarCombo(TipoMovimiento tipoMovimiento);

    List<TipoMovimientoOpcionDTO> listarTiposMovimiento();

    void validarTipoMovimiento(Integer idActividad, TipoMovimiento tipoEsperado);

    void eliminarActividad(Integer id);

    List<ActividadDTO> listarActividadesPorActividadEconomica(Integer idActividadEconomica);
}
