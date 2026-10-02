package com.kontagro.service.implementation;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Class.IngresoporActividadDTO;
import com.kontagro.dto.Converter.IngresoDTOConverter;
import com.kontagro.entities.Ingreso;
import com.kontagro.entities.enums.TipoMovimiento;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IIngresoRepository;
import com.kontagro.service.contracts.IActividadService;
import com.kontagro.service.contracts.IDocumentoSoporteService;
import com.kontagro.service.contracts.IIngresoService;
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
public class IngresoService implements IIngresoService {

    private final IIngresoRepository ingresoRepository;
    private final IngresoDTOConverter ingresoDTOConverter;
    private final IActividadService actividadService;
    private final IDocumentoSoporteService documentoSoporteService;

    @Override
    @Transactional
    public IngresoDTO crearIngreso(IngresoDTO ingresoDTO) {
        validarIngreso(ingresoDTO);
        return ingresoDTOConverter.convertToDTO(
                ingresoRepository.save(ingresoDTOConverter.convertToEntity(ingresoDTO))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public IngresoDTO consultarIngreso(Integer id) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MensajesError.INGRESO_NO_ENCONTRADO, id)
                ));
        return ingresoDTOConverter.convertToDTO(ingreso);
    }

    @Override
    @Transactional
    public IngresoDTO actualizarIngreso(IngresoDTO ingresoDTO) {
        if (ingresoDTO == null || ingresoDTO.getId() == null) {
            throw new BadRequestException("El ID del ingreso es obligatorio para actualizar.");
        }

        consultarIngreso(ingresoDTO.getId());
        validarIngreso(ingresoDTO);

        return ingresoDTOConverter.convertToDTO(
                ingresoRepository.save(ingresoDTOConverter.convertToEntity(ingresoDTO))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IngresoDTO> consultarIngreso(Pageable pageable) {
        return ingresoRepository.findAll(pageable).map(ingresoDTOConverter::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoporActividadDTO> consultarIngresoPorFecha(LocalDate fechaInicial, LocalDate fechaFinal) {

        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException(MensajesError.FECHAS_NULAS);
        }
        if (fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException(MensajesError.FECHA_INICIAL_MAYOR);
        }

        return ingresoRepository.findFechas(fechaInicial, fechaFinal);
    }

    @Override
    @Transactional
    public void eliminarIngreso(Integer id) {
        if (!ingresoRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(MensajesError.INGRESO_NO_ENCONTRADO, id));
        }
        documentoSoporteService.eliminarPorIngreso(id);
        ingresoRepository.deleteById(id);
    }

    private void validarIngreso(IngresoDTO ingresoDTO) {
        if (ingresoDTO == null) {
            throw new BadRequestException("Los datos del ingreso son obligatorios.");
        }
        if (ingresoDTO.getFecha() == null) {
            throw new BadRequestException("La fecha del ingreso es obligatoria.");
        }
        if (ingresoDTO.getValor() == null || ingresoDTO.getValor().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El valor del ingreso debe ser mayor que cero.");
        }
        actividadService.validarTipoMovimiento(ingresoDTO.getIdActividad(), TipoMovimiento.INGRESO);
    }
}
