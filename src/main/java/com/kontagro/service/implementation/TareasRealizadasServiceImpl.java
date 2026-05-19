package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.TareasRealizadasDTOConverter;
import com.kontagro.dto.TareasRealizadasDTO;
import com.kontagro.entities.TareasRealizadas;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.ITareasRealizadasRepository;
import com.kontagro.service.contracts.ITareasRealizadasService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TareasRealizadasServiceImpl implements ITareasRealizadasService {

    private final ITareasRealizadasRepository tareasRealizadasRepository;
    private final TareasRealizadasDTOConverter tareasRealizadasConverter;

    @Override
    public TareasRealizadasDTO crearTareaRealizada(TareasRealizadasDTO dto) {
        TareasRealizadas entidad = tareasRealizadasConverter.convertToEntity(dto);
        TareasRealizadas guardada = tareasRealizadasRepository.save(entidad);
        return tareasRealizadasConverter.convertToDTO(guardada);
    }

    @Override
    public TareasRealizadasDTO consultarTareaRealizada(Integer id) {
        Optional<TareasRealizadas> tareaOptional = tareasRealizadasRepository.findById(id);

        if (tareaOptional.isPresent()) {
            return tareasRealizadasConverter.convertToDTO(tareaOptional.get());
        } else {
            throw new ResourceNotFoundException(String.format(MensajesError.TAREA_REALIZADA_ID_NO_EXISTE, id));
        }
    }

    @Override
    public TareasRealizadasDTO actualizarTareaRealizada(TareasRealizadasDTO dto) {
        TareasRealizadasDTO consulta = consultarTareaRealizada(dto.getId());

        if (consulta == null) {
            throw new ResourceNotFoundException(MensajesError.ACTIVIDAD_NO_EXISTE);
        }

        TareasRealizadas entidadModificar = tareasRealizadasConverter.convertToEntity(dto);
        return tareasRealizadasConverter.convertToDTO(tareasRealizadasRepository.save(entidadModificar));
    }

    @Override
    public List<TareasRealizadasDTO> listarTareasRealizadas() {
        List<TareasRealizadas> lista = tareasRealizadasRepository.findAll();
        return tareasRealizadasConverter.convertToDTOList(lista);
    }
}