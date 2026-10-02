package com.kontagro.reports.implementation;

import com.kontagro.dto.Class.DocumentoSoporteDTO;
import com.kontagro.dto.ContabilidadResumenDTO;
import com.kontagro.dto.MovimientoContableDTO;
import com.kontagro.dto.ObligacionPendienteDTO;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class ContabilidadPdfReportGenerator {

    private static final Color VERDE = new Color(46, 125, 50);
    private static final Color VERDE_OSCURO = new Color(20, 83, 45);
    private static final Color VERDE_SUAVE = new Color(238, 248, 239);
    private static final Color VERDE_MUY_SUAVE = new Color(247, 251, 247);
    private static final Color GRIS_TEXTO = new Color(71, 84, 103);
    private static final Color GRIS_OSCURO = new Color(52, 64, 84);
    private static final Color GRIS_BORDE = new Color(226, 232, 240);
    private static final Color GRIS_FONDO = new Color(248, 250, 252);
    private static final Color AMBAR = new Color(180, 83, 9);
    private static final Color ROJO = new Color(180, 35, 24);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MONEDA = crearFormatoMoneda();

    public byte[] generar(ContabilidadResumenDTO resumen) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.A4, 38, 38, 52, 54);
            PdfWriter writer = PdfWriter.getInstance(documento, salida);
            writer.setPageEvent(new PiePagina());
            documento.open();

            agregarEncabezado(documento, resumen);
            agregarResumen(documento, resumen);
            agregarMovimientos(documento, resumen.getMovimientos());
            agregarObligacionesPendientes(documento, resumen.getObligacionesPendientes());
            agregarDocumentosSoporte(documento, resumen.getDocumentosSoporte());
            agregarNotaMetodologica(documento);

            documento.close();
            return salida.toByteArray();
        } catch (DocumentException | IOException ex) {
            throw new IllegalStateException("No fue posible generar el reporte contable en PDF.", ex);
        }
    }

    private void agregarEncabezado(Document documento, ContabilidadResumenDTO resumen)
            throws DocumentException, IOException {
        PdfPTable header = new PdfPTable(new float[]{1.1f, 1.9f});
        header.setWidthPercentage(100);

        PdfPCell logoCell = celdaSinBorde();
        ClassPathResource logoResource = new ClassPathResource("static/img/logo-kontagro.png");
        try (InputStream input = logoResource.getInputStream()) {
            Image logo = Image.getInstance(input.readAllBytes());
            logo.scaleToFit(190, 52);
            logo.setAlignment(Image.ALIGN_LEFT);
            logoCell.addElement(logo);
        }
        header.addCell(logoCell);

        PdfPCell titleCell = celdaSinBorde();
        Paragraph title = new Paragraph("Reporte financiero", fuente(20, Font.BOLD, VERDE_OSCURO));
        title.setAlignment(Element.ALIGN_RIGHT);
        Paragraph subtitle = new Paragraph(
                "Periodo " + resumen.getFechaInicial().format(FECHA) + " al " + resumen.getFechaFinal().format(FECHA),
                fuente(10, Font.NORMAL, GRIS_TEXTO)
        );
        subtitle.setAlignment(Element.ALIGN_RIGHT);
        subtitle.setSpacingBefore(4);
        titleCell.addElement(title);
        titleCell.addElement(subtitle);
        header.addCell(titleCell);
        documento.add(header);

        PdfPTable accent = new PdfPTable(1);
        accent.setWidthPercentage(100);
        accent.setSpacingBefore(9);
        accent.setSpacingAfter(11);
        PdfPCell accentCell = new PdfPCell();
        accentCell.setFixedHeight(2.5f);
        accentCell.setBorder(Rectangle.NO_BORDER);
        accentCell.setBackgroundColor(VERDE);
        accent.addCell(accentCell);
        documento.add(accent);

        Paragraph generated = new Paragraph(
                "Generado el " + resumen.getGeneradoEn().format(FECHA_HORA)
                        + " | Moneda: pesos colombianos (COP)",
                fuente(8.6f, Font.NORMAL, GRIS_TEXTO)
        );
        generated.setSpacingAfter(15);
        documento.add(generated);
    }

    private void agregarResumen(Document documento, ContabilidadResumenDTO resumen) throws DocumentException {
        agregarTituloSeccion(documento, "Resumen financiero",
                "Consolidado de ingresos, egresos operativos, nómina y obligaciones laborales pendientes.");

        PdfPTable cards = new PdfPTable(3);
        cards.setWidthPercentage(100);
        cards.setWidths(new float[]{1, 1, 1});
        cards.setSpacingAfter(10);

        cards.addCell(tarjeta("Ingresos", resumen.getTotalIngresos(), VERDE_OSCURO));
        cards.addCell(tarjeta("Egresos operativos", resumen.getTotalEgresosOperativos(), GRIS_OSCURO));
        cards.addCell(tarjeta("Nómina pagada", resumen.getTotalNominaPagada(), GRIS_OSCURO));
        cards.addCell(tarjeta("Total gastos", resumen.getTotalGastos(), AMBAR));
        cards.addCell(tarjeta("Resultado neto", resumen.getResultadoNeto(), colorResultado(resumen.getResultadoNeto())));
        cards.addCell(tarjeta("Pagos pendientes", resumen.getPagosPendientes(), AMBAR));
        documento.add(cards);

        PdfPTable result = new PdfPTable(new float[]{1.6f, 1f});
        result.setWidthPercentage(100);
        result.setSpacingAfter(18);

        PdfPCell left = new PdfPCell();
        left.setPadding(12);
        left.setBorderColor(VERDE);
        left.setBackgroundColor(VERDE_SUAVE);
        left.addElement(new Paragraph("Resultado proyectado", fuente(9.2f, Font.BOLD, VERDE_OSCURO)));
        left.addElement(new Paragraph(
                "Resultado neto después de considerar las obligaciones laborales que todavía están pendientes de liquidar.",
                fuente(8.3f, Font.NORMAL, GRIS_TEXTO)
        ));
        result.addCell(left);

        PdfPCell right = new PdfPCell();
        right.setPadding(12);
        right.setBorderColor(VERDE);
        right.setBackgroundColor(VERDE_MUY_SUAVE);
        Paragraph value = new Paragraph(
                formatearMoneda(resumen.getResultadoProyectado()),
                fuente(16.5f, Font.BOLD, colorResultado(resumen.getResultadoProyectado()))
        );
        value.setAlignment(Element.ALIGN_RIGHT);
        Paragraph state = new Paragraph(
                etiquetaResultado(resumen.getResultadoProyectado()),
                fuente(8.5f, Font.BOLD, colorResultado(resumen.getResultadoProyectado()))
        );
        state.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(value);
        right.addElement(state);
        result.addCell(right);
        documento.add(result);
    }

    private void agregarMovimientos(Document documento, List<MovimientoContableDTO> movimientos)
            throws DocumentException {
        agregarTituloSeccion(documento, "Movimientos del periodo",
                "Operaciones que componen el resultado financiero realizado.");

        if (movimientos == null || movimientos.isEmpty()) {
            documento.add(mensajeVacio("No se registraron movimientos en el periodo seleccionado."));
            return;
        }

        PdfPTable tabla = new PdfPTable(new float[]{0.70f, 0.72f, 1.25f, 1.45f, 1.0f, 1.15f, 0.72f});
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSpacingAfter(18);
        agregarEncabezadoTabla(tabla, "Fecha");
        agregarEncabezadoTabla(tabla, "Origen");
        agregarEncabezadoTabla(tabla, "Concepto");
        agregarEncabezadoTabla(tabla, "Tercero");
        agregarEncabezadoTabla(tabla, "Documento");
        agregarEncabezadoTabla(tabla, "Valor");
        agregarEncabezadoTabla(tabla, "Soportes");

        for (int i = 0; i < movimientos.size(); i++) {
            MovimientoContableDTO movimiento = movimientos.get(i);
            Color fondo = i % 2 == 0 ? Color.WHITE : GRIS_FONDO;
            tabla.addCell(celdaDato(movimiento.getFecha().format(FECHA), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(etiquetaOrigen(movimiento.getOrigen()), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(movimiento.getConcepto(), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(movimiento.getTercero(), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(movimiento.getDocumento(), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(formatearMoneda(movimiento.getValor()), fondo, Element.ALIGN_RIGHT));
            tabla.addCell(celdaDato(movimiento.getSoportes() > 0 ? String.valueOf(movimiento.getSoportes()) : "-", fondo, Element.ALIGN_CENTER));
        }
        documento.add(tabla);
    }

    private void agregarObligacionesPendientes(Document documento, List<ObligacionPendienteDTO> obligaciones)
            throws DocumentException {
        agregarTituloSeccion(documento, "Obligaciones laborales pendientes",
                "Pagos aún no liquidados en nómina. Se muestran como proyección y no como gasto realizado.");

        if (obligaciones == null || obligaciones.isEmpty()) {
            documento.add(mensajeVacio("No hay obligaciones laborales pendientes en el periodo consultado."));
            return;
        }

        PdfPTable tabla = new PdfPTable(new float[]{1.05f, 1.15f, 1.55f, 1.0f, 0.95f, 0.95f, 1.0f});
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSpacingAfter(18);
        agregarEncabezadoTabla(tabla, "Trabajador");
        agregarEncabezadoTabla(tabla, "Tarea");
        agregarEncabezadoTabla(tabla, "Días trabajados");
        agregarEncabezadoTabla(tabla, "Periodo");
        agregarEncabezadoTabla(tabla, "Valor");
        agregarEncabezadoTabla(tabla, "Descuentos");
        agregarEncabezadoTabla(tabla, "Neto pendiente");

        for (int i = 0; i < obligaciones.size(); i++) {
            ObligacionPendienteDTO obligacion = obligaciones.get(i);
            Color fondo = i % 2 == 0 ? Color.WHITE : GRIS_FONDO;
            tabla.addCell(celdaDato(obligacion.getTrabajador(), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(obligacion.getTarea(), fondo, Element.ALIGN_LEFT));
            String dias = obligacion.getDiasTrabajados() == null || obligacion.getDiasTrabajados().isEmpty()
                    ? "-"
                    : obligacion.getDiasTrabajados().stream().map(FECHA::format).collect(java.util.stream.Collectors.joining(", "));
            tabla.addCell(celdaDato(dias, fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(
                    obligacion.getFechaInicial().format(FECHA) + " - " + obligacion.getFechaFinal().format(FECHA),
                    fondo,
                    Element.ALIGN_LEFT
            ));
            tabla.addCell(celdaDato(formatearMoneda(obligacion.getValorPagar()), fondo, Element.ALIGN_RIGHT));
            tabla.addCell(celdaDato(formatearMoneda(obligacion.getDescuentos()), fondo, Element.ALIGN_RIGHT));
            tabla.addCell(celdaDato(formatearMoneda(obligacion.getValorNeto()), fondo, Element.ALIGN_RIGHT));
        }
        documento.add(tabla);
    }


    private void agregarDocumentosSoporte(Document documento, List<DocumentoSoporteDTO> documentos)
            throws DocumentException {
        agregarTituloSeccion(documento, "Documentos soporte",
                "Relación de facturas, recibos y demás soportes cargados a los movimientos del periodo.");

        if (documentos == null || documentos.isEmpty()) {
            documento.add(mensajeVacio("No se cargaron documentos soporte para los movimientos del periodo."));
            return;
        }

        PdfPTable tabla = new PdfPTable(new float[]{0.70f, 0.78f, 0.95f, 1.20f, 1.15f, 1.45f});
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSpacingAfter(18);
        agregarEncabezadoTabla(tabla, "Fecha");
        agregarEncabezadoTabla(tabla, "Origen");
        agregarEncabezadoTabla(tabla, "Tipo");
        agregarEncabezadoTabla(tabla, "Documento");
        agregarEncabezadoTabla(tabla, "Tercero");
        agregarEncabezadoTabla(tabla, "Archivo");

        for (int i = 0; i < documentos.size(); i++) {
            DocumentoSoporteDTO soporte = documentos.get(i);
            Color fondo = i % 2 == 0 ? Color.WHITE : GRIS_FONDO;
            tabla.addCell(celdaDato(soporte.getFechaDocumento() == null ? "-" : soporte.getFechaDocumento().format(FECHA), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(etiquetaOrigen(soporte.getOrigen()), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(soporte.getTipoDocumento() == null ? "-" : soporte.getTipoDocumento().name().replace('_', ' '), fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(soporte.getNumeroDocumento(), fondo, Element.ALIGN_LEFT));
            String tercero = soporte.getNombreTercero();
            if (soporte.getIdentificacionTercero() != null && !soporte.getIdentificacionTercero().isBlank()) {
                tercero = (tercero == null ? "" : tercero + " - ") + soporte.getIdentificacionTercero();
            }
            tabla.addCell(celdaDato(tercero, fondo, Element.ALIGN_LEFT));
            tabla.addCell(celdaDato(soporte.getNombreArchivo(), fondo, Element.ALIGN_LEFT));
        }
        documento.add(tabla);
    }

    private void agregarNotaMetodologica(Document documento) throws DocumentException {
        PdfPTable note = new PdfPTable(1);
        note.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(GRIS_BORDE);
        cell.setBackgroundColor(GRIS_FONDO);
        cell.addElement(new Paragraph("Cómo leer este reporte", fuente(8.8f, Font.BOLD, GRIS_OSCURO)));
        cell.addElement(new Paragraph(
                "El resultado neto resta a los ingresos los egresos operativos y la nómina efectivamente liquidada. "
                        + "El resultado proyectado también descuenta los pagos laborales pendientes. Las liquidaciones anuladas no se contabilizan.",
                fuente(8.1f, Font.NORMAL, GRIS_TEXTO)
        ));
        note.addCell(cell);
        documento.add(note);
    }

    private void agregarTituloSeccion(Document documento, String titulo, String descripcion) throws DocumentException {
        Paragraph heading = new Paragraph(titulo, fuente(12.8f, Font.BOLD, VERDE_OSCURO));
        heading.setSpacingBefore(2);
        heading.setSpacingAfter(2);
        documento.add(heading);
        Paragraph help = new Paragraph(descripcion, fuente(8.4f, Font.NORMAL, GRIS_TEXTO));
        help.setSpacingAfter(8);
        documento.add(help);
    }

    private PdfPCell tarjeta(String label, BigDecimal value, Color valueColor) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(GRIS_BORDE);
        cell.setBackgroundColor(Color.WHITE);
        cell.addElement(new Paragraph(label, fuente(8.3f, Font.NORMAL, GRIS_TEXTO)));
        cell.addElement(new Paragraph(formatearMoneda(value), fuente(11.8f, Font.BOLD, valueColor)));
        return cell;
    }

    private PdfPTable mensajeVacio(String texto) {
        PdfPTable empty = new PdfPTable(1);
        empty.setWidthPercentage(100);
        empty.setSpacingAfter(18);
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente(8.8f, Font.NORMAL, GRIS_TEXTO)));
        cell.setPadding(12);
        cell.setBorderColor(GRIS_BORDE);
        cell.setBackgroundColor(GRIS_FONDO);
        empty.addCell(cell);
        return empty;
    }

    private void agregarEncabezadoTabla(PdfPTable tabla, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente(8.1f, Font.BOLD, Color.WHITE)));
        cell.setPadding(6.5f);
        cell.setBackgroundColor(VERDE);
        cell.setBorderColor(VERDE);
        tabla.addCell(cell);
    }

    private PdfPCell celdaDato(String texto, Color fondo, int alineacion) {
        String value = texto == null || texto.isBlank() ? "-" : texto;
        PdfPCell cell = new PdfPCell(new Phrase(value, fuente(7.8f, Font.NORMAL, GRIS_TEXTO)));
        cell.setPadding(6.5f);
        cell.setBorderColor(GRIS_BORDE);
        cell.setBackgroundColor(fondo);
        cell.setHorizontalAlignment(alineacion);
        return cell;
    }

    private PdfPCell celdaSinBorde() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0);
        return cell;
    }

    private Font fuente(float size, int style, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
    }

    private String formatearMoneda(BigDecimal valor) {
        return MONEDA.format(valor == null ? BigDecimal.ZERO : valor);
    }

    private Color colorResultado(BigDecimal valor) {
        return valor != null && valor.signum() < 0 ? ROJO : VERDE_OSCURO;
    }

    private String etiquetaResultado(BigDecimal valor) {
        if (valor == null || valor.signum() == 0) {
            return "Punto de equilibrio";
        }
        return valor.signum() > 0 ? "Ganancia proyectada" : "Pérdida proyectada";
    }

    private String etiquetaOrigen(String origen) {
        if ("NOMINA".equals(origen)) {
            return "Nómina";
        }
        if ("EGRESO".equals(origen)) {
            return "Egreso";
        }
        return "Ingreso";
    }

    private static NumberFormat crearFormatoMoneda() {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMinimumFractionDigits(0);
        formato.setMaximumFractionDigits(0);
        return formato;
    }

    private static final class PiePagina extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable footer = new PdfPTable(2);
            try {
                footer.setWidths(new float[]{4, 1});
                footer.setTotalWidth(document.right() - document.left());

                PdfPCell brand = new PdfPCell(new Phrase(
                        "KontAgro - Tecnología para el Agro",
                        FontFactory.getFont(FontFactory.HELVETICA, 7.8f, Font.NORMAL, GRIS_TEXTO)
                ));
                brand.setBorder(Rectangle.TOP);
                brand.setBorderColor(GRIS_BORDE);
                brand.setPaddingTop(6);
                brand.setHorizontalAlignment(Element.ALIGN_LEFT);
                footer.addCell(brand);

                PdfPCell page = new PdfPCell(new Phrase(
                        "Página " + writer.getPageNumber(),
                        FontFactory.getFont(FontFactory.HELVETICA, 7.8f, Font.NORMAL, GRIS_TEXTO)
                ));
                page.setBorder(Rectangle.TOP);
                page.setBorderColor(GRIS_BORDE);
                page.setPaddingTop(6);
                page.setHorizontalAlignment(Element.ALIGN_RIGHT);
                footer.addCell(page);

                footer.writeSelectedRows(0, -1, document.left(), document.bottom() - 12, writer.getDirectContent());
            } catch (DocumentException ignored) {
                // El pie de página no debe interrumpir la generación del reporte.
            }
        }
    }
}
