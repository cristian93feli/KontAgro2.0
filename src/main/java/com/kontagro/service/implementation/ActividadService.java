package com.kontagro.service.implementation;

import com.kontagro.dto.Class.ActividadDTO;
import com.kontagro.dto.TipoMovimientoOpcionDTO;
import com.kontagro.dto.Converter.ActividadDTOConverter;
import com.kontagro.entities.Actividad;
import com.kontagro.entities.ActividadEconomica;
import com.kontagro.entities.enums.TipoMovimiento;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IActividadEconomicaRepository;
import com.kontagro.repository.IActividadRepository;
import com.kontagro.repository.IEgresoRepository;
import com.kontagro.repository.IIngresoRepository;
import com.kontagro.service.contracts.IActividadService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadService implements IActividadService {

    private final IActividadRepository actividadRepository;
    private final IActividadEconomicaRepository actividadEconomicaRepository;
    private final ActividadDTOConverter actividadDTOConverter;
    private final IIngresoRepository ingresoRepository;
    private final IEgresoRepository egresoRepository;

    @Override
    @Transactional
    public ActividadDTO crearActividad(ActividadDTO dto) {
        validarDatosActividad(dto);
        Actividad entidad = new Actividad();
        entidad.setNombreActividad(dto.getNombreActividad().trim());
        entidad.setActividadEconomica(resolverCategoriaFinanciera(dto));
        return actividadDTOConverter.convertToDTO(actividadRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public ActividadDTO consultarActividad(Integer id) {
        return actividadDTOConverter.convertToDTO(obtenerActividad(id));
    }

    @Override
    @Transactional
    public ActividadDTO actualizarActividad(ActividadDTO dto) {
        if (dto == null || dto.getIdActividad() == null) {
            throw new BadRequestException("El ID de la actividad es obligatorio para actualizar.");
        }

        validarDatosActividad(dto);
        Actividad existente = obtenerActividad(dto.getIdActividad());
        ActividadEconomica nuevaCategoria = resolverCategoriaFinanciera(dto);
        validarCambioTipoMovimiento(existente, nuevaCategoria.getTipoMovimiento());

        existente.setNombreActividad(dto.getNombreActividad().trim());
        existente.setActividadEconomica(nuevaCategoria);
        return actividadDTOConverter.convertToDTO(actividadRepository.save(existente));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActividadDTO> consultarActividad(Pageable pageable) {
        // Una colección vacía es un resultado válido; no debe disparar una alerta de error.
        return actividadRepository.findAll(pageable).map(actividadDTOConverter::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadDTO> listarCombo() {
        return convertirCombo(actividadRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadDTO> listarCombo(TipoMovimiento tipoMovimiento) {
        if (tipoMovimiento == null) {
            return listarCombo();
        }
        return convertirCombo(
                actividadRepository.findByActividadEconomica_TipoMovimientoOrderByNombreActividadAsc(tipoMovimiento)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoMovimientoOpcionDTO> listarTiposMovimiento() {
        return List.of(
                new TipoMovimientoOpcionDTO(TipoMovimiento.INGRESO, "Ingreso"),
                new TipoMovimientoOpcionDTO(TipoMovimiento.EGRESO, "Egreso")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public void validarTipoMovimiento(Integer idActividad, TipoMovimiento tipoEsperado) {
        if (tipoEsperado == null) {
            throw new BadRequestException("El tipo de movimiento esperado es obligatorio.");
        }

        Actividad actividad = obtenerActividad(idActividad);
        TipoMovimiento tipoActual = obtenerTipoMovimiento(actividad);
        if (tipoActual == null) {
            throw new BadRequestException(
                    "La actividad todavía no tiene una clasificación financiera. " +
                    "Edítela y seleccione si corresponde a Ingresos o Egresos."
            );
        }
        if (tipoActual != tipoEsperado) {
            throw new BadRequestException(String.format(
                    "La actividad '%s' está clasificada como %s y no puede utilizarse en %s.",
                    actividad.getNombreActividad(), etiquetaPlural(tipoActual), etiquetaPlural(tipoEsperado)
            ));
        }
    }

    @Override
    @Transactional
    public void eliminarActividad(Integer id) {
        Actividad actividad = obtenerActividad(id);
        if (ingresoRepository.existsByActividad_IdActividad(id) || egresoRepository.existsByActividad_IdActividad(id)) {
            throw new BadRequestException("No se puede eliminar una actividad que ya tiene movimientos financieros asociados.");
        }
        actividadRepository.delete(actividad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadDTO> listarActividadesPorActividadEconomica(Integer idActividadEconomica) {
        return actividadDTOConverter.convertToDTOList(
                actividadRepository.findByActividadEconomicaId(idActividadEconomica)
        );
    }

    private void validarCambioTipoMovimiento(Actividad existente, TipoMovimiento nuevoTipo) {
        TipoMovimiento actual = obtenerTipoMovimiento(existente);
        if (actual == nuevoTipo) {
            return;
        }

        boolean tieneIngresos = ingresoRepository.existsByActividad_IdActividad(existente.getIdActividad());
        boolean tieneEgresos = egresoRepository.existsByActividad_IdActividad(existente.getIdActividad());
        if (tieneIngresos && nuevoTipo != TipoMovimiento.INGRESO) {
            throw new BadRequestException("No se puede cambiar esta actividad a Egresos porque ya tiene ingresos registrados.");
        }
        if (tieneEgresos && nuevoTipo != TipoMovimiento.EGRESO) {
            throw new BadRequestException("No se puede cambiar esta actividad a Ingresos porque ya tiene egresos registrados.");
        }
    }

    private TipoMovimiento obtenerTipoMovimiento(Actividad actividad) {
        return actividad.getActividadEconomica() == null
                ? null
                : actividad.getActividadEconomica().getTipoMovimiento();
    }

    private Actividad obtenerActividad(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID de la actividad es obligatorio.");
        }
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MensajesError.ACTIVIDAD_NO_ENCONTRADA, id)
                ));
    }

    /**
     * Mantiene la relación histórica con actividad_economica, pero el contrato del
     * formulario se basa en TipoMovimiento. Así Angular no depende de que existan
     * previamente filas sembradas en la base para mostrar Ingreso/Egreso.
     */
    private ActividadEconomica resolverCategoriaFinanciera(ActividadDTO dto) {
        if (dto.getTipoMovimiento() != null) {
            return obtenerOCrearCategoriaPorTipo(dto.getTipoMovimiento());
        }

        if (dto.getIdActividadEconomica() != null) {
            ActividadEconomica categoria = obtenerActividadEconomica(dto.getIdActividadEconomica());
            if (categoria.getTipoMovimiento() == null) {
                throw new BadRequestException(
                        "La clasificación financiera seleccionada no está configurada como Ingreso o Egreso."
                );
            }
            return categoria;
        }

        throw new BadRequestException("Debe seleccionar si la actividad corresponde a Ingresos o Egresos.");
    }

    private ActividadEconomica obtenerOCrearCategoriaPorTipo(TipoMovimiento tipoMovimiento) {
        return actividadEconomicaRepository.findFirstByTipoMovimiento(tipoMovimiento)
                .orElseGet(() -> {
                    String nombre = tipoMovimiento == TipoMovimiento.INGRESO ? "Ingresos" : "Egresos";
                    ActividadEconomica categoria = actividadEconomicaRepository
                            .findFirstByNombreActividadEconomicaIgnoreCase(nombre)
                            .orElseGet(ActividadEconomica::new);
                    categoria.setNombreActividadEconomica(nombre);
                    categoria.setTipoMovimiento(tipoMovimiento);
                    return actividadEconomicaRepository.save(categoria);
                });
    }

    private ActividadEconomica obtenerActividadEconomica(Integer id) {
        return actividadEconomicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La clasificación financiera seleccionada no existe."));
    }

    private void validarDatosActividad(ActividadDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos de la actividad son obligatorios.");
        }
        if (dto.getNombreActividad() == null || dto.getNombreActividad().trim().isEmpty()) {
            throw new BadRequestException("El nombre de la actividad es obligatorio.");
        }
        if (dto.getNombreActividad().trim().length() > 255) {
            throw new BadRequestException("El nombre de la actividad no puede superar los 255 caracteres.");
        }
        if (dto.getTipoMovimiento() == null && dto.getIdActividadEconomica() == null) {
            throw new BadRequestException("Debe seleccionar si la actividad corresponde a Ingresos o Egresos.");
        }
    }

    private String etiquetaPlural(TipoMovimiento tipo) {
        return tipo == TipoMovimiento.INGRESO ? "Ingresos" : "Egresos";
    }

    private List<ActividadDTO> convertirCombo(List<Actividad> actividades) {
        return actividades.stream().map(actividadDTOConverter::convertToDTO).toList();
    }
}
