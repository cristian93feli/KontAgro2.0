package com.kontagro.service.contracts;

import com.kontagro.dto.Class.DocumentoSoporteDTO;
import com.kontagro.entities.enums.TipoDocumentoSoporte;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface IDocumentoSoporteService {
    DocumentoSoporteDTO agregarAIngreso(Integer idIngreso, TipoDocumentoSoporte tipoDocumento,
                                         String numeroDocumento, LocalDate fechaDocumento,
                                         String nombreTercero, String identificacionTercero,
                                         MultipartFile archivo);
    DocumentoSoporteDTO agregarAEgreso(Integer idEgreso, TipoDocumentoSoporte tipoDocumento,
                                        String numeroDocumento, LocalDate fechaDocumento,
                                        String nombreTercero, String identificacionTercero,
                                        MultipartFile archivo);
    List<DocumentoSoporteDTO> listarPorIngreso(Integer idIngreso);
    List<DocumentoSoporteDTO> listarPorEgreso(Integer idEgreso);
    DocumentoDescarga obtenerParaDescarga(Long id);
    void eliminar(Long id);
    void eliminarPorIngreso(Integer idIngreso);
    void eliminarPorEgreso(Integer idEgreso);

    record DocumentoDescarga(Resource resource, String nombreArchivo, String tipoMime) {}
}
