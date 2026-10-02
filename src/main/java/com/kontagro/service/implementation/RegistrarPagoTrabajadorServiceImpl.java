package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.RegistrarPagoTrabajadorDTOConverter;
import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
import com.kontagro.entities.PagoDiaTrabajado;
import com.kontagro.entities.RegistrarPagoTrabajador;
import com.kontagro.entities.TareasRealizadas;
import com.kontagro.entities.Trabajador;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ConflictException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.ILiquidacionNominaDetalleRepository;
import com.kontagro.repository.IRegistrarPagoTrabajadorRepository;
import com.kontagro.repository.ITareasRealizadasRepository;
import com.kontagro.repository.ITrabajadorRepository;
import com.kontagro.service.contracts.IRegistrarPagoTrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrarPagoTrabajadorServiceImpl implements IRegistrarPagoTrabajadorService {

    private static final BigDecimal CERO = BigDecimal.ZERO;
    private static final DateTimeFormatter FORMATO_FECHA_LARGA = DateTimeFormatter.ofPattern(
            "EEEE d 'de' MMMM 'de' uuuu",
            Locale.forLanguageTag("es-CO")
    );

    private final IRegistrarPagoTrabajadorRepository pagoRepository;
    private final ITrabajadorRepository trabajadorRepository;
    private final ITareasRealizadasRepository tareaRepository;
    private final ILiquidacionNominaDetalleRepository detalleNominaRepository;
    private final RegistrarPagoTrabajadorDTOConverter converter;

    @Override
    @Transactional
    public RegistrarPagoTrabajadorDTO crearPagoTrabajador(RegistrarPagoTrabajadorDTO dto) {
        validarDatosPago(dto);
        RegistrarPagoTrabajador entidad = new RegistrarPagoTrabajador();
        aplicarDatosEditables(entidad, dto, true);
        entidad.setPagado(false);
        return convertirConHistorial(pagoRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrarPagoTrabajadorDTO consultarPagoTrabajador(Integer id) {
        return convertirConHistorial(obtenerPago(id));
    }

    @Override
    @Transactional
    public RegistrarPagoTrabajadorDTO actualizarPagoTrabajador(RegistrarPagoTrabajadorDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BadRequestException("El ID del pago es obligatorio para actualizar.");
        }

        validarDatosPago(dto);
        RegistrarPagoTrabajador entidad = obtenerPago(dto.getId());
        if (entidad.isPagado()) {
            throw new BadRequestException("No se puede editar un pago que ya fue liquidado en nómina.");
        }

        aplicarDatosEditables(entidad, dto, false);
        return convertirConHistorial(pagoRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrarPagoTrabajadorDTO> listarPagosTrabajadores() {
        List<RegistrarPagoTrabajador> lista = pagoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<RegistrarPagoTrabajadorDTO> dtos = converter.convertToDTOList(lista);
        marcarHistorialNomina(dtos);
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrarPagoTrabajadorDTO> listarPagosPendientesPorTrabajador(Integer idTrabajador) {
        if (idTrabajador == null || !trabajadorRepository.existsById(idTrabajador)) {
            throw new ResourceNotFoundException("El trabajador indicado no existe.");
        }
        List<RegistrarPagoTrabajadorDTO> dtos = converter.convertToDTOList(
                pagoRepository.findByTrabajador_IdAndPagadoFalseOrderByFechaInicialAsc(idTrabajador)
        );
        marcarHistorialNomina(dtos);
        return dtos;
    }

    @Override
    @Transactional
    public void eliminarPagoTrabajador(Integer id) {
        RegistrarPagoTrabajador pago = obtenerPago(id);
        if (pago.isPagado()) {
            throw new BadRequestException("No se puede eliminar un pago liquidado. Anule primero la liquidación de nómina correspondiente.");
        }
        if (detalleNominaRepository.existsByPago_Id(id)) {
            throw new BadRequestException("No se puede eliminar el pago porque hace parte del historial de una liquidación de nómina.");
        }
        pagoRepository.delete(pago);
    }

    private RegistrarPagoTrabajadorDTO convertirConHistorial(RegistrarPagoTrabajador pago) {
        RegistrarPagoTrabajadorDTO dto = converter.convertToDTO(pago);
        dto.setTieneHistorialNomina(pago.getId() != null && detalleNominaRepository.existsByPago_Id(pago.getId()));
        return dto;
    }

    private void marcarHistorialNomina(List<RegistrarPagoTrabajadorDTO> dtos) {
        List<Integer> ids = dtos.stream()
                .map(RegistrarPagoTrabajadorDTO::getId)
                .filter(Objects::nonNull)
                .toList();
        if (ids.isEmpty()) {
            return;
        }

        Set<Integer> conHistorial = new HashSet<>(detalleNominaRepository.findPagoIdsConHistorial(ids));
        dtos.forEach(dto -> dto.setTieneHistorialNomina(
                dto.getId() != null && conHistorial.contains(dto.getId())
        ));
    }

    private RegistrarPagoTrabajador obtenerPago(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID del pago es obligatorio.");
        }
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("El registro de pago con ID %d no fue encontrado.", id)
                ));
    }

    private void aplicarDatosEditables(RegistrarPagoTrabajador entidad, RegistrarPagoTrabajadorDTO dto, boolean nuevoPago) {
        // Bloqueamos al trabajador mientras se validan y persisten sus jornadas para
        // evitar registros simultáneos de la misma fecha desde dos solicitudes.
        Trabajador trabajador = trabajadorRepository.findByIdForUpdate(dto.getIdTrabajador())
                .orElseThrow(() -> new ResourceNotFoundException("El trabajador seleccionado no existe."));

        TareasRealizadas tarea = tareaRepository.findById(dto.getIdTarea())
                .orElseThrow(() -> new ResourceNotFoundException("La tarea seleccionada no existe."));

        boolean conservaMismaTarea = !nuevoPago
                && entidad.getTarea() != null
                && entidad.getTarea().getId().equals(tarea.getId());
        if (!tarea.isActivo() && !conservaMismaTarea) {
            throw new BadRequestException("La tarea seleccionada está inactiva. Seleccione una tarea activa.");
        }

        List<LocalDate> dias = normalizarDias(dto.getDiasTrabajados());
        validarJornadasDuplicadas(
                trabajador,
                dias,
                nuevoPago ? null : entidad.getId()
        );

        entidad.setTrabajador(trabajador);
        entidad.setTarea(tarea);
        entidad.setFechaInicial(dias.get(0));
        entidad.setFechaFinal(dias.get(dias.size() - 1));
        sincronizarDiasTrabajados(entidad, dias);
        entidad.setValorPagar(dto.getValorPagar());
        entidad.setValorDescuentos(normalizarDescuento(dto.getValorDescuentos()));
        entidad.setObservaciones(normalizarTexto(dto.getObservaciones()));
    }

    private void validarDatosPago(RegistrarPagoTrabajadorDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del pago son obligatorios.");
        }
        if (dto.getIdTrabajador() == null) {
            throw new BadRequestException("Debe seleccionar un trabajador.");
        }
        if (dto.getIdTarea() == null) {
            throw new BadRequestException("Debe seleccionar una tarea.");
        }

        normalizarDias(dto.getDiasTrabajados());

        if (dto.getValorPagar() == null || dto.getValorPagar().compareTo(CERO) <= 0) {
            throw new BadRequestException("El valor a pagar debe ser mayor que cero.");
        }

        BigDecimal descuento = normalizarDescuento(dto.getValorDescuentos());
        if (descuento.compareTo(CERO) < 0) {
            throw new BadRequestException("El valor de descuentos no puede ser negativo.");
        }
        if (descuento.compareTo(dto.getValorPagar()) > 0) {
            throw new BadRequestException("Los descuentos no pueden superar el valor a pagar del periodo.");
        }
        if (dto.getObservaciones() != null && dto.getObservaciones().trim().length() > 255) {
            throw new BadRequestException("Las observaciones no pueden superar los 255 caracteres.");
        }
    }

    private List<LocalDate> normalizarDias(List<LocalDate> dias) {
        if (dias == null || dias.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos un día trabajado.");
        }
        if (dias.stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException("La selección de días trabajados contiene una fecha inválida.");
        }
        if (dias.stream().anyMatch(fecha -> fecha.isAfter(LocalDate.now()))) {
            throw new BadRequestException("No puede registrar días trabajados posteriores a la fecha actual.");
        }

        Set<LocalDate> unicos = new LinkedHashSet<>(dias);
        if (unicos.size() != dias.size()) {
            throw new BadRequestException("No puede registrar el mismo día trabajado más de una vez.");
        }

        List<LocalDate> ordenados = new ArrayList<>(unicos);
        ordenados.sort(LocalDate::compareTo);
        return ordenados;
    }

    private void validarJornadasDuplicadas(
            Trabajador trabajador,
            List<LocalDate> dias,
            Integer idPagoExcluir
    ) {
        List<LocalDate> conflictos = idPagoExcluir == null
                ? pagoRepository.findDiasRegistradosPorTrabajador(trabajador.getId(), dias)
                : pagoRepository.findDiasRegistradosPorTrabajadorExcluyendoPago(
                        trabajador.getId(),
                        dias,
                        idPagoExcluir
                );

        if (conflictos.isEmpty()) {
            return;
        }

        String nombreTrabajador = trabajador.getNombre() == null || trabajador.getNombre().isBlank()
                ? "seleccionado"
                : trabajador.getNombre().trim();

        if (conflictos.size() == 1) {
            throw new ConflictException(String.format(
                    "El trabajador %s ya tiene una jornada registrada para el %s. Retire esa fecha o edite el pago existente.",
                    nombreTrabajador,
                    formatearFecha(conflictos.get(0))
            ));
        }

        String fechas = conflictos.stream()
                .map(this::formatearFecha)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        throw new ConflictException(String.format(
                "El trabajador %s ya tiene jornadas registradas en estas fechas: %s. Retire las fechas repetidas o edite los pagos existentes.",
                nombreTrabajador,
                fechas
        ));
    }

    /**
     * Sincroniza de forma diferencial la colección de jornadas. Las fechas que
     * siguen presentes conservan su entidad e ID; solamente se eliminan las que
     * el usuario quitó explícitamente y se crean las fechas nuevas.
     */
    private void sincronizarDiasTrabajados(RegistrarPagoTrabajador pago, List<LocalDate> diasDeseados) {
        Set<LocalDate> fechasDeseadas = new LinkedHashSet<>(diasDeseados);

        pago.getDiasTrabajados().removeIf(dia -> !fechasDeseadas.contains(dia.getFecha()));

        Set<LocalDate> fechasActuales = pago.getDiasTrabajados().stream()
                .map(PagoDiaTrabajado::getFecha)
                .collect(java.util.stream.Collectors.toSet());

        for (LocalDate fecha : diasDeseados) {
            if (fechasActuales.contains(fecha)) {
                continue;
            }
            PagoDiaTrabajado dia = new PagoDiaTrabajado();
            dia.setPago(pago);
            dia.setFecha(fecha);
            pago.getDiasTrabajados().add(dia);
        }
    }

    private String formatearFecha(LocalDate fecha) {
        return fecha.format(FORMATO_FECHA_LARGA);
    }

    private BigDecimal normalizarDescuento(BigDecimal valor) {
        return valor == null ? CERO : valor;
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
