package com.kontagro.service.implementation;

import com.kontagro.dto.Class.EgresoDTO;
import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Converter.EgresoDTOConverter;
import com.kontagro.entities.Egreso;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IEgresoRepository;
import com.kontagro.service.contracts.IEgresoService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EgresoService implements IEgresoService {

    private final IEgresoRepository iEgresoRepository;
    private final EgresoDTOConverter egresoDTOConverter;


    @Override
    public EgresoDTO crearEgreso(EgresoDTO egresoDTO) {
        return (egresoDTOConverter.convertToDTO
                (iEgresoRepository.save(egresoDTOConverter.convertToEntity(egresoDTO))));
    }

    @Override
    public EgresoDTO consultarEgreso(Integer id) {

        Optional<Egreso> resultado = iEgresoRepository.findById(id);

        if (resultado.isPresent()) {
            Egreso egreso = resultado.get();
            return egresoDTOConverter.convertToDTO(egreso);
        } else {
            throw new ResourceNotFoundException(String.format(MensajesError.EGRESO_NO_ENCONTRADO, id));
        }
    }

    @Override
    public EgresoDTO actualizarEgreso(EgresoDTO egresoDTO) {

        consultarEgreso(egresoDTO.getId());
            return (egresoDTOConverter.convertToDTO
                    (iEgresoRepository.save(egresoDTOConverter.convertToEntity(egresoDTO))));
        }

    @Override
        public Page<EgresoDTO> consultarEgreso(Pageable pageable) {

            return iEgresoRepository
                    .findAll(pageable)
                    .map(egresoDTOConverter::convertToDTO);
    }

    @Override
    public List<EgresoDTO> consultarEgresoPorFecha(LocalDate fecha_inicial, LocalDate fecha_final)
            throws BadRequestException {

        if (fecha_inicial == null || fecha_final == null) {
            throw new BadRequestException(MensajesError.FECHAS_NULAS);
        }

        if (fecha_inicial.isAfter(fecha_final)) {
            throw new RuntimeException( MensajesError.FECHA_INICIAL_MAYOR);
        }
        List<EgresoDTO> egresos = egresoDTOConverter.convertToDTOList(iEgresoRepository.findByFechaBetween(fecha_inicial, fecha_final));;

        return egresos;
    }

    @Override
    public void eliminarEgreso(Integer id) {
        if (!iEgresoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format(MensajesError.EGRESO_NO_ENCONTRADO, id));
        }
        iEgresoRepository.deleteById(id);
    }
}
