package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.TareasRealizadasDTOConverter;
import com.kontagro.dto.TareasRealizadasDTO;
import com.kontagro.entities.TareasRealizadas;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IRegistrarPagoTrabajadorRepository;
import com.kontagro.repository.ITareasRealizadasRepository;
import com.kontagro.service.contracts.ITareasRealizadasService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TareasRealizadasServiceImpl implements ITareasRealizadasService {

    private final ITareasRealizadasRepository tareasRepository;
    private final IRegistrarPagoTrabajadorRepository pagoRepository;
    private final TareasRealizadasDTOConverter converter;

    @Override
    @Transactional
    public TareasRealizadasDTO crearTareaRealizada(TareasRealizadasDTO dto) {
        validar(dto, null);
        TareasRealizadas tarea = new TareasRealizadas();
        aplicarDatos(tarea, dto);
        tarea.setActivo(true);
        return converter.convertToDTO(tareasRepository.save(tarea));
    }

    @Override
    @Transactional(readOnly = true)
    public TareasRealizadasDTO consultarTareaRealizada(Integer id) {
        return converter.convertToDTO(obtenerTarea(id));
    }

    @Override
    @Transactional
    public TareasRealizadasDTO actualizarTareaRealizada(TareasRealizadasDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BadRequestException("El ID de la tarea es obligatorio para actualizar.");
        }
        validar(dto, dto.getId());
        TareasRealizadas tarea = obtenerTarea(dto.getId());
        aplicarDatos(tarea, dto);
        return converter.convertToDTO(tareasRepository.save(tarea));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareasRealizadasDTO> listarTareasRealizadas() {
        return converter.convertToDTOList(tareasRepository.findAllByOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareasRealizadasDTO> listarTareasActivas() {
        return converter.convertToDTOList(tareasRepository.findByActivoTrueOrderByNombreAsc());
    }

    @Override
    @Transactional
    public TareasRealizadasDTO cambiarEstado(Integer id, boolean activo) {
        TareasRealizadas tarea = obtenerTarea(id);
        tarea.setActivo(activo);
        return converter.convertToDTO(tareasRepository.save(tarea));
    }

    @Override
    @Transactional
    public void eliminarTareaRealizada(Integer id) {
        TareasRealizadas tarea = obtenerTarea(id);
        if (pagoRepository.existsByTarea_Id(id)) {
            throw new BadRequestException(
                    "La tarea ya tiene pagos asociados y no puede eliminarse. Desactívela para conservar el historial."
            );
        }
        tareasRepository.delete(tarea);
    }

    private TareasRealizadas obtenerTarea(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID de la tarea es obligatorio.");
        }
        return tareasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MensajesError.TAREA_REALIZADA_ID_NO_EXISTE, id)
                ));
    }

    private void aplicarDatos(TareasRealizadas tarea, TareasRealizadasDTO dto) {
        tarea.setNombre(dto.getNombre().trim());
        tarea.setDescripcion(normalizar(dto.getDescripcion()));
    }

    private void validar(TareasRealizadasDTO dto, Integer idActual) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre de la tarea es obligatorio.");
        }
        String nombre = dto.getNombre().trim();
        if (nombre.length() > 120) {
            throw new BadRequestException("El nombre de la tarea no puede superar los 120 caracteres.");
        }
        if (dto.getDescripcion() != null && dto.getDescripcion().trim().length() > 255) {
            throw new BadRequestException("La descripción no puede superar los 255 caracteres.");
        }
        boolean duplicada = idActual == null
                ? tareasRepository.existsByNombreIgnoreCase(nombre)
                : tareasRepository.existsByNombreIgnoreCaseAndIdNot(nombre, idActual);
        if (duplicada) {
            throw new BadRequestException("Ya existe una tarea con ese nombre.");
        }
    }

    private String normalizar(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim();
    }
}
