package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.RegistrarPagoTrabajadorDTOConverter;
import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
import com.kontagro.entities.RegistrarPagoTrabajador;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IRegistrarPagoTrabajadorRepository;
import com.kontagro.service.contracts.IRegistrarPagoTrabajadorService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrarPagoTrabajadorServiceImpl implements IRegistrarPagoTrabajadorService {

    private final IRegistrarPagoTrabajadorRepository pagoTrabajadorRepository;
    private final RegistrarPagoTrabajadorDTOConverter pagoTrabajadorConverter;

    @Override
    public RegistrarPagoTrabajadorDTO crearPagoTrabajador(RegistrarPagoTrabajadorDTO dto) {
        RegistrarPagoTrabajador entidad = pagoTrabajadorConverter.convertToEntity(dto);
        RegistrarPagoTrabajador guardada = pagoTrabajadorRepository.save(entidad);
        return pagoTrabajadorConverter.convertToDTO(guardada);
    }

    @Override
    public RegistrarPagoTrabajadorDTO consultarPagoTrabajador(Integer id) {
        Optional<RegistrarPagoTrabajador> pagoOptional = pagoTrabajadorRepository.findById(id);

        if (pagoOptional.isPresent()) {
            return pagoTrabajadorConverter.convertToDTO(pagoOptional.get());
        } else {
            throw new ResourceNotFoundException(String.format("El registro de pago con ID %d no fue encontrado.", id));
        }
    }

    @Override
    public RegistrarPagoTrabajadorDTO actualizarPagoTrabajador(RegistrarPagoTrabajadorDTO dto) {
        RegistrarPagoTrabajadorDTO consulta = consultarPagoTrabajador(dto.getId());

        if (consulta == null) {
            throw new ResourceNotFoundException(MensajesError.NO_EXISTE_REGISTROS);
        }

        RegistrarPagoTrabajador entidadModificar = pagoTrabajadorConverter.convertToEntity(dto);
        return pagoTrabajadorConverter.convertToDTO(pagoTrabajadorRepository.save(entidadModificar));
    }

    @Override
    public List<RegistrarPagoTrabajadorDTO> listarPagosTrabajadores() {
        List<RegistrarPagoTrabajador> lista = pagoTrabajadorRepository.findAll();
        return pagoTrabajadorConverter.convertToDTOList(lista);
    }
}