package com.kontagro.service.implementation;

import com.kontagro.dto.Converter.LiquidacionNominaDTOConverter;
import com.kontagro.dto.LiquidacionNominaDTO;
import com.kontagro.dto.LiquidarNominaRequestDTO;
import com.kontagro.dto.RegistrarPagoTrabajadorDTO;
import com.kontagro.entities.LiquidacionNomina;
import com.kontagro.entities.LiquidacionNominaDetalle;
import com.kontagro.entities.RegistrarPagoTrabajador;
import com.kontagro.entities.Trabajador;
import com.kontagro.entities.enums.EstadoLiquidacion;
import com.kontagro.entities.enums.EstadoPago;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.ILiquidacionNominaDetalleRepository;
import com.kontagro.repository.ILiquidacionNominaRepository;
import com.kontagro.repository.IRegistrarPagoTrabajadorRepository;
import com.kontagro.repository.ITrabajadorRepository;
import com.kontagro.service.contracts.ILiquidacionNominaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LiquidacionNominaServiceImpl implements ILiquidacionNominaService {

    private static final BigDecimal CERO = BigDecimal.ZERO;

    private final ILiquidacionNominaRepository liquidacionRepository;
    private final ILiquidacionNominaDetalleRepository detalleRepository;
    private final IRegistrarPagoTrabajadorRepository pagoRepository;
    private final ITrabajadorRepository trabajadorRepository;
    private final LiquidacionNominaDTOConverter liquidacionConverter;

    @Override
    @Transactional
    public LiquidacionNominaDTO liquidarNomina(LiquidarNominaRequestDTO request) {
        validarSolicitud(request);

        Trabajador trabajador = trabajadorRepository.findById(request.getIdTrabajador())
                .orElseThrow(() -> new ResourceNotFoundException("El trabajador seleccionado no existe."));

        Set<Integer> idsUnicos = new LinkedHashSet<>(request.getIdsPagos());
        if (idsUnicos.size() != request.getIdsPagos().size()) {
            throw new BadRequestException("La selección contiene pagos repetidos.");
        }

        List<RegistrarPagoTrabajador> pagos = pagoRepository.findAllByIdForUpdate(idsUnicos);
        if (pagos.size() != idsUnicos.size()) {
            throw new ResourceNotFoundException("Uno o varios pagos seleccionados ya no existen.");
        }

        validarPagosParaLiquidacion(pagos, trabajador.getId());

        LocalDate fechaInicial = pagos.stream()
                .map(RegistrarPagoTrabajador::getFechaInicial)
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new BadRequestException("No fue posible calcular la fecha inicial de la liquidación."));
        LocalDate fechaFinal = pagos.stream()
                .map(RegistrarPagoTrabajador::getFechaFinal)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new BadRequestException("No fue posible calcular la fecha final de la liquidación."));
        BigDecimal totalTrabajado = pagos.stream()
                .map(RegistrarPagoTrabajador::getValorPagar)
                .reduce(CERO, BigDecimal::add);
        BigDecimal totalDescuentos = pagos.stream()
                .map(pago -> pago.getValorDescuentos() == null ? CERO : pago.getValorDescuentos())
                .reduce(CERO, BigDecimal::add);
        BigDecimal totalPagado = totalTrabajado.subtract(totalDescuentos);

        if (totalPagado.compareTo(CERO) < 0) {
            throw new BadRequestException("El total de descuentos no puede superar el valor total trabajado.");
        }

        LiquidacionNomina liquidacion = new LiquidacionNomina();
        liquidacion.setTrabajador(trabajador);
        liquidacion.setFechaInicialPagado(fechaInicial);
        liquidacion.setFechaFinalPagado(fechaFinal);
        liquidacion.setValorTotalTrabajado(totalTrabajado);
        liquidacion.setValorTotalDescuentos(totalDescuentos);
        liquidacion.setValorTotalPagado(totalPagado);
        liquidacion.setFechaLiquidacion(LocalDateTime.now());
        liquidacion.setEstado(EstadoLiquidacion.ACTIVA);
        LiquidacionNomina guardada = liquidacionRepository.save(liquidacion);

        List<LiquidacionNominaDetalle> detalles = new ArrayList<>();
        for (RegistrarPagoTrabajador pago : pagos) {
            pago.setPagado(true);

            LiquidacionNominaDetalle detalle = new LiquidacionNominaDetalle();
            detalle.setLiquidacion(guardada);
            detalle.setPago(pago);
            detalle.setIdTarea(pago.getTarea().getId());
            detalle.setNombreTarea(pago.getTarea().getNombre());
            detalle.setFechaInicial(pago.getFechaInicial());
            detalle.setFechaFinal(pago.getFechaFinal());
            detalle.setDiasTrabajados(serializarDias(pago));
            detalle.setValorPagar(pago.getValorPagar());
            detalle.setValorDescuentos(pago.getValorDescuentos() == null ? CERO : pago.getValorDescuentos());
            detalle.setObservaciones(pago.getObservaciones());
            detalles.add(detalle);
        }

        pagoRepository.saveAll(pagos);
        detalleRepository.saveAll(detalles);

        LiquidacionNominaDTO dto = liquidacionConverter.convertToDTO(guardada);
        dto.setIdsPagos(new ArrayList<>(idsUnicos));
        dto.setAnulable(true);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public LiquidacionNominaDTO consultarLiquidacion(Integer id) {
        LiquidacionNomina liquidacion = obtenerLiquidacion(id);
        LiquidacionNominaDTO dto = liquidacionConverter.convertToDTO(liquidacion);
        dto.setIdsPagos(obtenerIdsPagos(id));
        dto.setAnulable(dto.getEstado() != EstadoLiquidacion.ANULADA && !dto.getIdsPagos().isEmpty());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiquidacionNominaDTO> listarLiquidaciones() {
        List<LiquidacionNomina> liquidaciones = liquidacionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<Integer> ids = liquidaciones.stream().map(LiquidacionNomina::getId).toList();
        Set<Integer> idsConDetalle = ids.isEmpty()
                ? Set.of()
                : new java.util.HashSet<>(detalleRepository.findLiquidacionIdsConDetalle(ids));

        return liquidaciones.stream()
                .map(liquidacion -> {
                    LiquidacionNominaDTO dto = liquidacionConverter.convertToDTO(liquidacion);
                    dto.setAnulable(dto.getEstado() != EstadoLiquidacion.ANULADA && idsConDetalle.contains(liquidacion.getId()));
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrarPagoTrabajadorDTO> listarPagosLiquidacion(Integer idLiquidacion) {
        LiquidacionNomina liquidacion = obtenerLiquidacion(idLiquidacion);
        return detalleRepository.findByLiquidacionId(idLiquidacion)
                .stream()
                .map(detalle -> convertirDetalleHistorico(detalle, liquidacion))
                .toList();
    }

    @Override
    @Transactional
    public LiquidacionNominaDTO anularLiquidacion(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID de la liquidación es obligatorio.");
        }

        LiquidacionNomina liquidacion = liquidacionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("La liquidación con ID %d no fue encontrada.", id)
                ));

        EstadoLiquidacion estadoActual = liquidacion.getEstado() == null
                ? EstadoLiquidacion.ACTIVA
                : liquidacion.getEstado();
        if (estadoActual == EstadoLiquidacion.ANULADA) {
            throw new BadRequestException("La liquidación ya se encuentra anulada.");
        }

        List<Integer> idsPagos = obtenerIdsPagos(id);
        if (idsPagos.isEmpty()) {
            throw new BadRequestException("La liquidación no tiene pagos asociados y no puede anularse automáticamente.");
        }

        List<RegistrarPagoTrabajador> pagos = pagoRepository.findAllByIdForUpdate(idsPagos);
        if (pagos.size() != idsPagos.size()) {
            throw new BadRequestException("No fue posible recuperar todos los pagos asociados a la liquidación.");
        }

        pagos.forEach(pago -> pago.setPagado(false));
        pagoRepository.saveAll(pagos);

        liquidacion.setEstado(EstadoLiquidacion.ANULADA);
        liquidacion.setFechaAnulacion(LocalDateTime.now());
        LiquidacionNomina actualizada = liquidacionRepository.save(liquidacion);

        LiquidacionNominaDTO dto = liquidacionConverter.convertToDTO(actualizada);
        dto.setIdsPagos(idsPagos);
        dto.setAnulable(false);
        return dto;
    }

    private RegistrarPagoTrabajadorDTO convertirDetalleHistorico(
            LiquidacionNominaDetalle detalle,
            LiquidacionNomina liquidacion
    ) {
        RegistrarPagoTrabajadorDTO dto = new RegistrarPagoTrabajadorDTO();
        dto.setId(detalle.getPago() != null ? detalle.getPago().getId() : null);
        dto.setIdTrabajador(liquidacion.getTrabajador() != null ? liquidacion.getTrabajador().getId() : null);
        dto.setIdTarea(detalle.getIdTarea());
        dto.setNombreTarea(detalle.getNombreTarea());
        dto.setFechaInicial(detalle.getFechaInicial());
        dto.setFechaFinal(detalle.getFechaFinal());
        dto.setDiasTrabajados(deserializarDias(detalle.getDiasTrabajados(), detalle.getFechaInicial(), detalle.getFechaFinal()));
        dto.setValorPagar(detalle.getValorPagar());
        dto.setValorDescuentos(detalle.getValorDescuentos());
        dto.setObservaciones(detalle.getObservaciones());
        dto.setEstado(EstadoPago.PAGADO);
        dto.setTieneHistorialNomina(true);
        return dto;
    }

    private void validarSolicitud(LiquidarNominaRequestDTO request) {
        if (request == null || request.getIdTrabajador() == null) {
            throw new BadRequestException("Debe seleccionar un trabajador.");
        }
        if (request.getIdsPagos() == null || request.getIdsPagos().isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos un pago pendiente para liquidar.");
        }
        if (request.getIdsPagos().stream().anyMatch(id -> id == null)) {
            throw new BadRequestException("La selección de pagos contiene un ID inválido.");
        }
    }

    private void validarPagosParaLiquidacion(List<RegistrarPagoTrabajador> pagos, Integer idTrabajador) {
        for (RegistrarPagoTrabajador pago : pagos) {
            if (pago.getTrabajador() == null || !idTrabajador.equals(pago.getTrabajador().getId())) {
                throw new BadRequestException("Todos los pagos seleccionados deben pertenecer al trabajador elegido.");
            }
            if (pago.isPagado()) {
                throw new BadRequestException("Uno de los pagos seleccionados ya fue liquidado. Actualice la lista e intente nuevamente.");
            }
            if (pago.getTarea() == null) {
                throw new BadRequestException(
                        String.format("El pago %d no tiene una tarea asociada. Actualícelo antes de liquidar.", pago.getId())
                );
            }
            if (pago.getFechaInicial() == null || pago.getFechaFinal() == null || pago.getFechaInicial().isAfter(pago.getFechaFinal())) {
                throw new BadRequestException("Uno de los pagos contiene fechas de trabajo inválidas.");
            }
            if (pago.getDiasTrabajados() == null || pago.getDiasTrabajados().isEmpty()) {
                throw new BadRequestException(
                        String.format("El pago %d no tiene días trabajados registrados. Edítelo antes de liquidar.", pago.getId())
                );
            }
            if (pago.getValorPagar() == null || pago.getValorPagar().compareTo(CERO) <= 0) {
                throw new BadRequestException(
                        String.format("El pago %d no tiene un valor a pagar válido. Edítelo antes de liquidar.", pago.getId())
                );
            }

            BigDecimal descuento = pago.getValorDescuentos() == null ? CERO : pago.getValorDescuentos();
            if (descuento.compareTo(CERO) < 0 || descuento.compareTo(pago.getValorPagar()) > 0) {
                throw new BadRequestException(
                        String.format("El pago %d contiene un valor de descuentos inválido.", pago.getId())
                );
            }
        }
    }


    private String serializarDias(RegistrarPagoTrabajador pago) {
        if (pago.getDiasTrabajados() == null || pago.getDiasTrabajados().isEmpty()) return null;
        return pago.getDiasTrabajados().stream()
                .map(dia -> dia.getFecha().toString())
                .collect(java.util.stream.Collectors.joining(","));
    }

    private List<LocalDate> deserializarDias(String valor, LocalDate fechaInicial, LocalDate fechaFinal) {
        if (valor != null && !valor.isBlank()) {
            return java.util.Arrays.stream(valor.split(","))
                    .map(String::trim)
                    .filter(texto -> !texto.isBlank())
                    .map(LocalDate::parse)
                    .sorted()
                    .toList();
        }
        List<LocalDate> historicos = new ArrayList<>();
        if (fechaInicial != null) historicos.add(fechaInicial);
        if (fechaFinal != null && !fechaFinal.equals(fechaInicial)) historicos.add(fechaFinal);
        return historicos;
    }

    private LiquidacionNomina obtenerLiquidacion(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID de la liquidación es obligatorio.");
        }
        return liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("La liquidación con ID %d no fue encontrada.", id)
                ));
    }

    private List<Integer> obtenerIdsPagos(Integer idLiquidacion) {
        return detalleRepository.findByLiquidacionId(idLiquidacion)
                .stream()
                .map(detalle -> detalle.getPago().getId())
                .toList();
    }
}
