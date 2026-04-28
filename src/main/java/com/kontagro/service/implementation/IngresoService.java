package com.kontagro.service.implementation;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Class.IngresoporActividadDTO;
import com.kontagro.dto.Converter.IngresoDTOConverter;
import com.kontagro.dto.Converter.IngresoPorActividadConverter;
import com.kontagro.entities.Ingreso;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IIngresoRepository;
import com.kontagro.service.contracts.IIngresoService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngresoService implements IIngresoService {

    private final IIngresoRepository iIngresoRepository;
    private final IngresoDTOConverter ingresoDTOConverter;
    private final IngresoPorActividadConverter ingresoPorActividadConverter;

    @Override
    public IngresoDTO crearIngreso(IngresoDTO ingresoDTO) {
        return ingresoDTOConverter.convertToDTO(iIngresoRepository.save(ingresoDTOConverter.convertToEntity(ingresoDTO)));
    }

    @Override
    public IngresoDTO consultarIngreso(Integer id) {
        Optional<Ingreso> ingresoOptional = iIngresoRepository.findById(id);

        if (ingresoOptional.isPresent()) {
            return ingresoDTOConverter.convertToDTO(ingresoOptional.get());
        } else {
            throw new ResourceNotFoundException(String.format(MensajesError.INGRESO_NO_ENCONTRADO, id));
        }
    }

    @Override
    public IngresoDTO actualizarIngreso(IngresoDTO ingresoDTO) {
        consultarIngreso(ingresoDTO.getId());


            return ingresoDTOConverter.convertToDTO
                    (iIngresoRepository.save(ingresoDTOConverter.convertToEntity(ingresoDTO)));

    }

    @Override
    public List<IngresoDTO> consultarIngreso() {
        List<IngresoDTO> ingresoLista = ingresoDTOConverter.convertToDTOList(iIngresoRepository.findAll());

            return ingresoLista;

    }

    @Override
    public List<IngresoporActividadDTO> consultarIngresoPorFecha(LocalDate fechaInicial, LocalDate fechaFinal)
            throws BadRequestException {

        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException(MensajesError.FECHAS_NULAS);
        }

        if (fechaInicial.isAfter(fechaFinal)) {
            throw new RuntimeException(MensajesError.FECHA_INICIAL_MAYOR);
        }
        List<IngresoporActividadDTO> ingresos = iIngresoRepository.findFechas(fechaInicial, fechaFinal);

//        List<IngresoporActividadDTO> ingresos = iIngresoRepository.findFechas(fechaInicial,fechaFinal ).stream()
//                .map(ingresoPorActividadConverter::convertToDTO)
//                .collect(Collectors.toList());

        return ingresos;
    }

    @Override
    public void eliminarIngreso(Integer id) {
        if (!iIngresoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format(MensajesError.INGRESO_NO_ENCONTRADO, id));

        }
            iIngresoRepository.deleteById(id);

    }
}

