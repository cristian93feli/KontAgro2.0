package com.kontagro.dto;

import com.kontagro.entities.enums.TipoMovimiento;

/**
 * Opción de catálogo expuesta por el backend para evitar duplicar en Angular
 * los valores permitidos del dominio financiero.
 */
public record TipoMovimientoOpcionDTO(TipoMovimiento valor, String etiqueta) {
}
