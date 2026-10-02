package com.kontagro.service.implementation;

import com.kontagro.dto.Class.ActividadEconomicaDTO;
import com.kontagro.dto.Converter.ActividadEconomicaDTOConverter;
import com.kontagro.entities.ActividadEconomica;
import com.kontagro.entities.enums.TipoMovimiento;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IActividadEconomicaRepository;
import com.kontagro.service.contracts.IActividadEconomicaService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ActividadEconomicaServiceImpl implements IActividadEconomicaService {

    private final IActividadEconomicaRepository repository;
    private final ActividadEconomicaDTOConverter converter;

    @Override
    @Transactional
    public ActividadEconomicaDTO crearActividadEconomica(ActividadEconomicaDTO dto) {
        validarDto(dto, false);
        ActividadEconomica entidad = converter.convertToEntity(dto);
        entidad.setTipoMovimiento(resolverTipo(dto));
        return converter.convertToDTO(repository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public ActividadEconomicaDTO consultarActividadEconomica(Integer id) {
        return converter.convertToDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public ActividadEconomicaDTO actualizarActividadEconomica(ActividadEconomicaDTO dto) {
        validarDto(dto, true);
        ActividadEconomica existente = obtenerEntidad(dto.getId());
        existente.setNombreActividadEconomica(dto.getNombreActividadEconomica().trim());

        TipoMovimiento tipo = resolverTipo(dto);
        if (tipo != null) {
            existente.setTipoMovimiento(tipo);
        }

        return converter.convertToDTO(repository.save(existente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadEconomicaDTO> listarActividadesEconomicas() {
        return converter.convertToDTOList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadEconomicaDTO> listarClasificacionesFinancieras() {
        return converter.convertToDTOList(
                repository.findByTipoMovimientoIsNotNullOrderByNombreActividadEconomicaAsc()
        );
    }

    private ActividadEconomica obtenerEntidad(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID de la clasificación financiera es obligatorio.");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MensajesError.ACTIVIDAD_ECONOMICA_NO_ENCONTRADA, id)
                ));
    }

    private void validarDto(ActividadEconomicaDTO dto, boolean requiereId) {
        if (dto == null) {
            throw new BadRequestException("Los datos de la clasificación financiera son obligatorios.");
        }
        if (requiereId && dto.getId() == null) {
            throw new BadRequestException("El ID de la clasificación financiera es obligatorio para actualizar.");
        }
        if (dto.getNombreActividadEconomica() == null || dto.getNombreActividadEconomica().trim().isEmpty()) {
            throw new BadRequestException("El nombre de la clasificación financiera es obligatorio.");
        }
    }

    /**
     * Mantiene compatibilidad con las categorías históricas "Ingresos" y "Egresos".
     * Si el DTO ya incluye un tipo explícito, ese valor tiene prioridad.
     */
    private TipoMovimiento resolverTipo(ActividadEconomicaDTO dto) {
        if (dto.getTipoMovimiento() != null) {
            return dto.getTipoMovimiento();
        }

        String nombre = dto.getNombreActividadEconomica().trim().toLowerCase(Locale.ROOT);
        if (nombre.equals("ingreso") || nombre.equals("ingresos")) {
            return TipoMovimiento.INGRESO;
        }
        if (nombre.equals("egreso") || nombre.equals("egresos")) {
            return TipoMovimiento.EGRESO;
        }
        return null;
    }
}
