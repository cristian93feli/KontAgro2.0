package com.kontagro.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LiquidarNominaRequestDTO implements Serializable {
    private Integer idTrabajador;
    private List<Integer> idsPagos;
}
