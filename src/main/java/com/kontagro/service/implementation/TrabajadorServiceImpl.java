package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.TrabajadorDTOConverter;
import com.kontagro.dto.TrabajadorDTO;
import com.kontagro.entities.Trabajador;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.ILiquidacionNominaRepository;
import com.kontagro.repository.IRegistrarPagoTrabajadorRepository;
import com.kontagro.repository.ITrabajadorRepository;
import com.kontagro.service.contracts.ITrabajadorService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements ITrabajadorService {

    private final ITrabajadorRepository trabajadorRepository;
    private final IRegistrarPagoTrabajadorRepository pagoRepository;
    private final ILiquidacionNominaRepository liquidacionRepository;
    private final TrabajadorDTOConverter trabajadorDTOConverter;

    @Override
    @Transactional
    public TrabajadorDTO crearTrabajador(TrabajadorDTO dto) {
        validarNombre(dto);
        Trabajador entidad = trabajadorDTOConverter.convertToEntity(dto);
        entidad.setNombre(dto.getNombre().trim());
        return trabajadorDTOConverter.convertToDTO(trabajadorRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajadorDTO consultarTrabajador(Integer id) {
        return trabajadorDTOConverter.convertToDTO(obtenerTrabajador(id));
    }

    @Override
    @Transactional
    public TrabajadorDTO actualizarTrabajador(TrabajadorDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BadRequestException("El ID del trabajador es obligatorio para actualizar.");
        }
        validarNombre(dto);

        Trabajador trabajador = obtenerTrabajador(dto.getId());
        trabajador.setNombre(dto.getNombre().trim());
        return trabajadorDTOConverter.convertToDTO(trabajadorRepository.save(trabajador));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDTO> listarTrabajadores() {
        return trabajadorDTOConverter.convertToDTOList(trabajadorRepository.findAll());
    }

    @Override
    @Transactional
    public void eliminarTrabajador(Integer id) {
        Trabajador trabajador = obtenerTrabajador(id);
        if (pagoRepository.existsByTrabajador_Id(id) || liquidacionRepository.existsByTrabajador_Id(id)) {
            throw new BadRequestException(
                    "No se puede eliminar el trabajador porque tiene pagos o liquidaciones asociadas. Conserve el registro para mantener la trazabilidad contable."
            );
        }
        trabajadorRepository.delete(trabajador);
    }

    private Trabajador obtenerTrabajador(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID del trabajador es obligatorio.");
        }
        return trabajadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MensajesError.TRABAJADOR_NO_ENCONTRADA, id)));
    }

    private void validarNombre(TrabajadorDTO dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del trabajador es obligatorio.");
        }
    }
}
