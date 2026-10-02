package com.kontagro.service.implementation;

import com.kontagro.dto.Class.DocumentoSoporteDTO;
import com.kontagro.dto.ContabilidadResumenDTO;
import com.kontagro.dto.MovimientoContableDTO;
import com.kontagro.dto.ObligacionPendienteDTO;
import com.kontagro.entities.DocumentoSoporte;
import com.kontagro.entities.Egreso;
import com.kontagro.entities.Ingreso;
import com.kontagro.entities.LiquidacionNomina;
import com.kontagro.entities.RegistrarPagoTrabajador;
import com.kontagro.entities.enums.EstadoLiquidacion;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.repository.IDocumentoSoporteRepository;
import com.kontagro.repository.IEgresoRepository;
import com.kontagro.repository.IIngresoRepository;
import com.kontagro.repository.ILiquidacionNominaRepository;
import com.kontagro.repository.IRegistrarPagoTrabajadorRepository;
import com.kontagro.service.contracts.IContabilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContabilidadServiceImpl implements IContabilidadService {

    private static final BigDecimal CERO = BigDecimal.ZERO;

    private final IIngresoRepository ingresoRepository;
    private final IEgresoRepository egresoRepository;
    private final ILiquidacionNominaRepository liquidacionRepository;
    private final IRegistrarPagoTrabajadorRepository pagoRepository;
    private final IDocumentoSoporteRepository documentoRepository;

    @Override
    @Transactional(readOnly = true)
    public ContabilidadResumenDTO consultarResumen(LocalDate fechaInicial, LocalDate fechaFinal) {
        validarFechas(fechaInicial, fechaFinal);

        List<Ingreso> ingresos = ingresoRepository.findByFechaBetweenOrderByFechaAsc(fechaInicial, fechaFinal);
        List<Egreso> egresos = egresoRepository.findByFechaBetweenOrderByFechaAsc(fechaInicial, fechaFinal);
        List<LiquidacionNomina> nominas = liquidacionRepository
                .findByEstadoAndFechaLiquidacionGreaterThanEqualAndFechaLiquidacionLessThanOrderByFechaLiquidacionAsc(
                        EstadoLiquidacion.ACTIVA,
                        fechaInicial.atStartOfDay(),
                        fechaFinal.plusDays(1).atStartOfDay()
                );
        List<RegistrarPagoTrabajador> pendientes = pagoRepository
                .findByPagadoFalseAndFechaInicialLessThanEqualAndFechaFinalGreaterThanEqualOrderByFechaInicialAsc(
                        fechaFinal,
                        fechaInicial
                );

        BigDecimal totalIngresos = sumarIngresos(ingresos);
        BigDecimal totalEgresos = sumarEgresos(egresos);
        BigDecimal totalNomina = sumarNomina(nominas);
        BigDecimal totalGastos = totalEgresos.add(totalNomina);
        BigDecimal resultadoNeto = totalIngresos.subtract(totalGastos);
        BigDecimal pagosPendientes = sumarPendientesNetos(pendientes);
        BigDecimal resultadoProyectado = resultadoNeto.subtract(pagosPendientes);

        ContabilidadResumenDTO dto = new ContabilidadResumenDTO();
        dto.setFechaInicial(fechaInicial);
        dto.setFechaFinal(fechaFinal);
        dto.setGeneradoEn(LocalDateTime.now());
        dto.setTotalIngresos(totalIngresos);
        dto.setTotalEgresosOperativos(totalEgresos);
        dto.setTotalNominaPagada(totalNomina);
        dto.setTotalGastos(totalGastos);
        dto.setResultadoNeto(resultadoNeto);
        dto.setPagosPendientes(pagosPendientes);
        dto.setResultadoProyectado(resultadoProyectado);
        dto.setMovimientos(construirMovimientos(ingresos, egresos, nominas));
        dto.setObligacionesPendientes(construirObligacionesPendientes(pendientes));
        dto.setDocumentosSoporte(construirDocumentosSoporte(ingresos, egresos));
        return dto;
    }

    private List<MovimientoContableDTO> construirMovimientos(
            List<Ingreso> ingresos,
            List<Egreso> egresos,
            List<LiquidacionNomina> nominas
    ) {
        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        Map<Integer, List<DocumentoSoporte>> documentosIngreso = ingresos.isEmpty()
                ? Map.of()
                : documentoRepository.findByIngreso_IdIn(ingresos.stream().map(Ingreso::getId).toList()).stream()
                        .collect(Collectors.groupingBy(doc -> doc.getIngreso().getId()));
        Map<Integer, List<DocumentoSoporte>> documentosEgreso = egresos.isEmpty()
                ? Map.of()
                : documentoRepository.findByEgreso_IdIn(egresos.stream().map(Egreso::getId).toList()).stream()
                        .collect(Collectors.groupingBy(doc -> doc.getEgreso().getId()));

        ingresos.forEach(ingreso -> {
            MovimientoContableDTO movimiento = new MovimientoContableDTO(
                    ingreso.getFecha(),
                    "INGRESO",
                    ingreso.getActividad() != null ? ingreso.getActividad().getNombreActividad() : "Ingreso",
                    "Ingreso registrado",
                    nvl(ingreso.getValor())
            );
            aplicarDocumentoPrincipal(movimiento, documentosIngreso.getOrDefault(ingreso.getId(), List.of()));
            movimientos.add(movimiento);
        });

        egresos.forEach(egreso -> {
            MovimientoContableDTO movimiento = new MovimientoContableDTO(
                    egreso.getFecha(),
                    "EGRESO",
                    egreso.getActividad() != null ? egreso.getActividad().getNombreActividad() : "Egreso",
                    "Egreso operativo",
                    nvl(egreso.getValor())
            );
            aplicarDocumentoPrincipal(movimiento, documentosEgreso.getOrDefault(egreso.getId(), List.of()));
            movimientos.add(movimiento);
        });

        nominas.forEach(nomina -> movimientos.add(new MovimientoContableDTO(
                nomina.getFechaLiquidacion().toLocalDate(),
                "NOMINA",
                nomina.getTrabajador() != null ? "Nómina - " + nomina.getTrabajador().getNombre() : "Nómina",
                "Periodo " + nomina.getFechaInicialPagado() + " al " + nomina.getFechaFinalPagado(),
                nvl(nomina.getValorTotalPagado())
        )));

        movimientos.sort(
                Comparator.comparing(MovimientoContableDTO::getFecha)
                        .thenComparing(MovimientoContableDTO::getOrigen)
        );
        return movimientos;
    }

    private void aplicarDocumentoPrincipal(MovimientoContableDTO movimiento, List<DocumentoSoporte> documentos) {
        movimiento.setSoportes(documentos.size());
        if (documentos.isEmpty()) return;
        DocumentoSoporte principal = documentos.get(0);
        movimiento.setTercero(principal.getNombreTercero());
        String numero = principal.getNumeroDocumento();
        movimiento.setDocumento((principal.getTipoDocumento() == null ? "Soporte" : principal.getTipoDocumento().name().replace('_', ' '))
                + (numero == null || numero.isBlank() ? "" : " #" + numero));
    }


    private List<DocumentoSoporteDTO> construirDocumentosSoporte(List<Ingreso> ingresos, List<Egreso> egresos) {
        List<DocumentoSoporte> documentos = new ArrayList<>();
        if (!ingresos.isEmpty()) {
            documentos.addAll(documentoRepository.findByIngreso_IdIn(ingresos.stream().map(Ingreso::getId).toList()));
        }
        if (!egresos.isEmpty()) {
            documentos.addAll(documentoRepository.findByEgreso_IdIn(egresos.stream().map(Egreso::getId).toList()));
        }
        return documentos.stream()
                .sorted(Comparator.comparing(DocumentoSoporte::getFechaCarga))
                .map(documento -> {
                    DocumentoSoporteDTO dto = new DocumentoSoporteDTO();
                    dto.setId(documento.getId());
                    dto.setOrigen(documento.getIngreso() != null ? "INGRESO" : "EGRESO");
                    dto.setIdMovimiento(documento.getIngreso() != null ? documento.getIngreso().getId() : documento.getEgreso().getId());
                    dto.setTipoDocumento(documento.getTipoDocumento());
                    dto.setNumeroDocumento(documento.getNumeroDocumento());
                    dto.setFechaDocumento(documento.getFechaDocumento());
                    dto.setNombreTercero(documento.getNombreTercero());
                    dto.setIdentificacionTercero(documento.getIdentificacionTercero());
                    dto.setNombreArchivo(documento.getNombreArchivoOriginal());
                    dto.setTipoMime(documento.getTipoMime());
                    dto.setTamanoBytes(documento.getTamanoBytes());
                    dto.setFechaCarga(documento.getFechaCarga());
                    return dto;
                })
                .toList();
    }

    private List<ObligacionPendienteDTO> construirObligacionesPendientes(List<RegistrarPagoTrabajador> pendientes) {
        return pendientes.stream()
                .map(pago -> {
                    BigDecimal valorPagar = nvl(pago.getValorPagar());
                    BigDecimal descuentos = nvl(pago.getValorDescuentos());
                    BigDecimal neto = valorPagar.subtract(descuentos).max(CERO);
                    return new ObligacionPendienteDTO(
                            pago.getId(),
                            pago.getTrabajador() != null ? pago.getTrabajador().getNombre() : "Trabajador",
                            pago.getTarea() != null ? pago.getTarea().getNombre() : "Tarea histórica",
                            pago.getFechaInicial(),
                            pago.getFechaFinal(),
                            pago.getDiasTrabajados() == null ? List.of() : pago.getDiasTrabajados().stream().map(d -> d.getFecha()).toList(),
                            valorPagar,
                            descuentos,
                            neto
                    );
                })
                .toList();
    }

    private BigDecimal sumarIngresos(List<Ingreso> ingresos) {
        return ingresos.stream().map(Ingreso::getValor).map(this::nvl).reduce(CERO, BigDecimal::add);
    }

    private BigDecimal sumarEgresos(List<Egreso> egresos) {
        return egresos.stream().map(Egreso::getValor).map(this::nvl).reduce(CERO, BigDecimal::add);
    }

    private BigDecimal sumarNomina(List<LiquidacionNomina> nominas) {
        return nominas.stream().map(LiquidacionNomina::getValorTotalPagado).map(this::nvl).reduce(CERO, BigDecimal::add);
    }

    private BigDecimal sumarPendientesNetos(List<RegistrarPagoTrabajador> pendientes) {
        return pendientes.stream()
                .map(pago -> nvl(pago.getValorPagar()).subtract(nvl(pago.getValorDescuentos())))
                .filter(valor -> valor.compareTo(CERO) > 0)
                .reduce(CERO, BigDecimal::add);
    }

    private BigDecimal nvl(BigDecimal valor) {
        return valor == null ? CERO : valor;
    }

    private void validarFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException("Debe indicar la fecha inicial y final del reporte.");
        }
        if (fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");
        }
        if (fechaInicial.plusYears(5).isBefore(fechaFinal)) {
            throw new BadRequestException("El periodo consultado no puede superar cinco años.");
        }
    }
}
