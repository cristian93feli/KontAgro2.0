package com.kontagro.reports.implementation;

import com.kontagro.dto.Class.IngresoDTO;
import com.kontagro.dto.Class.IngresoporActividadDTO;
import com.kontagro.reports.contracts.IReportGenerator;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class IngresoReportGenerator implements IReportGenerator<IngresoporActividadDTO> {

    @Override
    public byte[] generateExcel(List<IngresoporActividadDTO> data) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Ingresos");

            // 1. Crear estilo para el encabezado (Negrita y fondo gris claro)
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 2. Crear fila de encabezados
            String[] columnas = {"ID Ingreso", "Actividad", "Fecha", "Valor"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. Llenar los datos
            int rowIdx = 1;
            double sumaTotal = 0; // Variable para acumular el total
            for (IngresoporActividadDTO ingreso : data) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(ingreso.getIdIngresos());
                // Aquí usamos el nombre de la actividad que ya vendrá en tu DTO
                row.createCell(1).setCellValue(ingreso.getNombreActividad());
                row.createCell(2).setCellValue(ingreso.getFecha().toString());
                // Sumamos al total y creamos la celda
                double valor = ingreso.getValor().doubleValue();
                row.createCell(3).setCellValue(valor);
                sumaTotal += valor;
            }

            // Creamos una fila separada por un espacio o inmediatamente después
            Row totalRow = sheet.createRow(rowIdx + 1);

        // Estilo para que resalte (Negrita)
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);

        // Celda de etiqueta
            Cell labelCell = totalRow.createCell(2); // La ponemos debajo de "Fecha"
            labelCell.setCellValue("TOTAL GENERAL:");
            labelCell.setCellStyle(totalStyle);

        // Celda con la suma
            Cell sumCell = totalRow.createCell(3); // Debajo de la columna "Valor"
            sumCell.setCellValue(sumaTotal);
            sumCell.setCellStyle(totalStyle);
            // 4. Ajustar el ancho de las columnas automáticamente
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    @Override
    public byte[] generatePdf(List<IngresoporActividadDTO> data) {
        // Lógica para OpenPDF...
        return new byte[0];
    }

}
