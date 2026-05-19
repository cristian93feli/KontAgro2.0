package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.LiquidacionNominaDTOConverter;
import com.kontagro.dto.LiquidacionNominaDTO;
import com.kontagro.entities.LiquidacionNomina;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.ILiquidacionNominaRepository;
import com.kontagro.service.contracts.ILiquidacionNominaService;
import com.kontagro.utils.MensajesError; // Asumiendo que agregarás constantes aquí
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LiquidacionNominaServiceImpl implements ILiquidacionNominaService {

    private final ILiquidacionNominaRepository liquidacionRepository;
    private final LiquidacionNominaDTOConverter liquidacionDTOConverter;

    @Override
    public LiquidacionNominaDTO crearLiquidacion(LiquidacionNominaDTO dto) {
        LiquidacionNomina entidad = liquidacionDTOConverter.convertToEntity(dto);
        LiquidacionNomina guardada = liquidacionRepository.save(entidad);
        return liquidacionDTOConverter.convertToDTO(guardada);
    }

    @Override
    public LiquidacionNominaDTO consultarLiquidacion(Integer id) {
        Optional<LiquidacionNomina> liquidacionOptional = liquidacionRepository.findById(id);

        if (liquidacionOptional.isPresent()) {
            return liquidacionDTOConverter.convertToDTO(liquidacionOptional.get());
        } else {
            throw new ResourceNotFoundException(String.format(MensajesError.LIQUIDACION_ID_NO_EXISTE, id));
        }
    }

    @Override
    public LiquidacionNominaDTO actualizarLiquidacion(LiquidacionNominaDTO dto) {
        LiquidacionNominaDTO consulta = consultarLiquidacion(dto.getId());

        if (consulta == null) {
            throw new ResourceNotFoundException(MensajesError.LIQUIDACION_NO_EXISTE);
        }

        LiquidacionNomina entidadAModificar = liquidacionDTOConverter.convertToEntity(dto);
        return liquidacionDTOConverter.convertToDTO(liquidacionRepository.save(entidadAModificar));
    }

    @Override
    public List<LiquidacionNominaDTO> listarLiquidaciones() {
        List<LiquidacionNomina> lista = liquidacionRepository.findAll();
        return liquidacionDTOConverter.convertToDTOList(lista);
    }
}