package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.ActividadEconomicaDTOConverter;
import com.kontagro.dto.Class.ActividadEconomicaDTO;
import com.kontagro.entities.ActividadEconomica;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IActividadEconomicaRepository;
import com.kontagro.service.contracts.IActividadEconomicaService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActividadEconomicaServiceImpl implements IActividadEconomicaService {

    private final IActividadEconomicaRepository repository;
    private final ActividadEconomicaDTOConverter converter;

    @Override
    public ActividadEconomicaDTO crearActividadEconomica(ActividadEconomicaDTO dto) {
        ActividadEconomica entidad = converter.convertToEntity(dto);
        ActividadEconomica guardada = repository.save(entidad);
        return converter.convertToDTO(guardada);
    }

    @Override
    public ActividadEconomicaDTO consultarActividadEconomica(Integer id) {
        Optional<ActividadEconomica> opcional = repository.findById(id);

        if (opcional.isPresent()) {
            return converter.convertToDTO(opcional.get());
        } else {
            // Evaluado de forma dinámica usando tus propiedades de MensajesError sin strings quemados
            throw new ResourceNotFoundException(String.format(MensajesError.ACTIVIDAD_ECONOMICA_NO_ENCONTRADA, id));
        }
    }

    @Override
    public ActividadEconomicaDTO actualizarActividadEconomica(ActividadEconomicaDTO dto) {
        ActividadEconomicaDTO consulta = consultarActividadEconomica(dto.getId());

        if (consulta == null) {
            // Validado directamente usando tu clase estática de control de errores
            throw new ResourceNotFoundException(MensajesError.ACTIVIDAD_ECONOMICA_NO_EXISTE);
        }

        ActividadEconomica entidadAModificar = converter.convertToEntity(dto);
        return converter.convertToDTO(repository.save(entidadAModificar));
    }

    @Override
    public List<ActividadEconomicaDTO> listarActividadesEconomicas() {
        List<ActividadEconomica> lista = repository.findAll();
        return converter.convertToDTOList(lista);
    }
}