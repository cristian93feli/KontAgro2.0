package com.kontagro.service.implementation;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.dto.Converter.EgresoDTOConverter;
import com.kontagro.entities.Egreso;
import com.kontagro.entities.enums.TipoMovimiento;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IEgresoRepository;
import com.kontagro.service.contracts.IActividadService;
import com.kontagro.service.contracts.IDocumentoSoporteService;
import com.kontagro.service.contracts.IEgresoService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EgresoService implements IEgresoService {

    private final IEgresoRepository egresoRepository;
    private final EgresoDTOConverter egresoDTOConverter;
    private final IActividadService actividadService;
    private final IDocumentoSoporteService documentoSoporteService;

    @Override
    @Transactional
    public EgresoDTO crearEgreso(EgresoDTO egresoDTO) {
        validarEgreso(egresoDTO);
        return egresoDTOConverter.convertToDTO(
                egresoRepository.save(egresoDTOConverter.convertToEntity(egresoDTO))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EgresoDTO consultarEgreso(Integer id) {
        Egreso egreso = egresoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MensajesError.EGRESO_NO_ENCONTRADO, id)
                ));
        return egresoDTOConverter.convertToDTO(egreso);
    }

    @Override
    @Transactional
    public EgresoDTO actualizarEgreso(EgresoDTO egresoDTO) {
        if (egresoDTO == null || egresoDTO.getId() == null) {
            throw new BadRequestException("El ID del egreso es obligatorio para actualizar.");
        }

        consultarEgreso(egresoDTO.getId());
        validarEgreso(egresoDTO);

        return egresoDTOConverter.convertToDTO(
                egresoRepository.save(egresoDTOConverter.convertToEntity(egresoDTO))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EgresoDTO> consultarEgreso(Pageable pageable) {
        return egresoRepository.findAll(pageable).map(egresoDTOConverter::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EgresoDTO> consultarEgresoPorFecha(LocalDate fechaInicial, LocalDate fechaFinal) {

        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException(MensajesError.FECHAS_NULAS);
        }
        if (fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException(MensajesError.FECHA_INICIAL_MAYOR);
        }

        return egresoDTOConverter.convertToDTOList(
                egresoRepository.findByFechaBetween(fechaInicial, fechaFinal)
        );
    }

    @Override
    @Transactional
    public void eliminarEgreso(Integer id) {
        if (!egresoRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(MensajesError.EGRESO_NO_ENCONTRADO, id));
        }
        documentoSoporteService.eliminarPorEgreso(id);
        egresoRepository.deleteById(id);
    }

    private void validarEgreso(EgresoDTO egresoDTO) {
        if (egresoDTO == null) {
            throw new BadRequestException("Los datos del egreso son obligatorios.");
        }
        if (egresoDTO.getFecha() == null) {
            throw new BadRequestException("La fecha del egreso es obligatoria.");
        }
        if (egresoDTO.getValor() == null || egresoDTO.getValor().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El valor del egreso debe ser mayor que cero.");
        }
        actividadService.validarTipoMovimiento(egresoDTO.getIdActividad(), TipoMovimiento.EGRESO);
    }
}
