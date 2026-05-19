package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.TrabajadorDTOConverter;
import com.kontagro.dto.TrabajadorDTO;
import com.kontagro.entities.Trabajador;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.ITrabajadorRepository;
import com.kontagro.service.contracts.ITrabajadorService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements ITrabajadorService {

    private final ITrabajadorRepository trabajadorRepository;
    private final TrabajadorDTOConverter trabajadorDTOConverter;

    @Override
    public TrabajadorDTO crearTrabajador(TrabajadorDTO dto) {
        Trabajador entidad = trabajadorDTOConverter.convertToEntity(dto);
        Trabajador guardado = trabajadorRepository.save(entidad);
        return trabajadorDTOConverter.convertToDTO(guardado);
    }

    @Override
    public TrabajadorDTO consultarTrabajador(Integer id) {
        Optional<Trabajador> trabajadorOptional = trabajadorRepository.findById(id);

        if (trabajadorOptional.isPresent()) {
            return trabajadorDTOConverter.convertToDTO(trabajadorOptional.get());
        } else {
            throw new ResourceNotFoundException(String.format(MensajesError.TRABAJADOR_NO_ENCONTRADA, id));
        }
    }

    @Override
    public TrabajadorDTO actualizarTrabajador(TrabajadorDTO dto) {
        TrabajadorDTO consulta = consultarTrabajador(dto.getId());

        if (consulta == null) {
            throw new ResourceNotFoundException(MensajesError.TRABAJADOR_NO_EXISTE);
        }

        Trabajador entidadAModificar = trabajadorDTOConverter.convertToEntity(dto);
        return trabajadorDTOConverter.convertToDTO(trabajadorRepository.save(entidadAModificar));
    }

    @Override
    public List<TrabajadorDTO> listarTrabajadores() {
        List<Trabajador> lista = trabajadorRepository.findAll();
        return trabajadorDTOConverter.convertToDTOList(lista);
    }
}