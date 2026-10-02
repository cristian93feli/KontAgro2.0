package com.kontagro.service.implementation;

import com.kontagro.dto.Class.DocumentoSoporteDTO;
import com.kontagro.entities.DocumentoSoporte;
import com.kontagro.entities.Egreso;
import com.kontagro.entities.Ingreso;
import com.kontagro.entities.enums.TipoDocumentoSoporte;
import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IDocumentoSoporteRepository;
import com.kontagro.repository.IEgresoRepository;
import com.kontagro.repository.IIngresoRepository;
import com.kontagro.service.contracts.IDocumentoSoporteService;
import com.kontagro.service.contracts.IDocumentoStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoSoporteServiceImpl implements IDocumentoSoporteService {

    private final IDocumentoSoporteRepository documentoRepository;
    private final IIngresoRepository ingresoRepository;
    private final IEgresoRepository egresoRepository;
    private final IDocumentoStorageService storageService;

    @Override
    @Transactional
    public DocumentoSoporteDTO agregarAIngreso(Integer idIngreso, TipoDocumentoSoporte tipoDocumento,
                                                String numeroDocumento, LocalDate fechaDocumento,
                                                String nombreTercero, String identificacionTercero,
                                                MultipartFile archivo) {
        Ingreso ingreso = ingresoRepository.findById(idIngreso)
                .orElseThrow(() -> new ResourceNotFoundException("El ingreso seleccionado no existe."));
        DocumentoSoporte documento = crearDocumento(tipoDocumento, numeroDocumento, fechaDocumento,
                nombreTercero, identificacionTercero, archivo);
        documento.setIngreso(ingreso);
        return convertir(documentoRepository.save(documento));
    }

    @Override
    @Transactional
    public DocumentoSoporteDTO agregarAEgreso(Integer idEgreso, TipoDocumentoSoporte tipoDocumento,
                                               String numeroDocumento, LocalDate fechaDocumento,
                                               String nombreTercero, String identificacionTercero,
                                               MultipartFile archivo) {
        Egreso egreso = egresoRepository.findById(idEgreso)
                .orElseThrow(() -> new ResourceNotFoundException("El egreso seleccionado no existe."));
        DocumentoSoporte documento = crearDocumento(tipoDocumento, numeroDocumento, fechaDocumento,
                nombreTercero, identificacionTercero, archivo);
        documento.setEgreso(egreso);
        return convertir(documentoRepository.save(documento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoSoporteDTO> listarPorIngreso(Integer idIngreso) {
        if (idIngreso == null) throw new BadRequestException("El ID del ingreso es obligatorio.");
        return documentoRepository.findByIngreso_IdOrderByFechaCargaDesc(idIngreso).stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoSoporteDTO> listarPorEgreso(Integer idEgreso) {
        if (idEgreso == null) throw new BadRequestException("El ID del egreso es obligatorio.");
        return documentoRepository.findByEgreso_IdOrderByFechaCargaDesc(idEgreso).stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoDescarga obtenerParaDescarga(Long id) {
        DocumentoSoporte documento = obtener(id);
        Resource resource = storageService.cargar(documento.getStorageKey());
        return new DocumentoDescarga(resource, documento.getNombreArchivoOriginal(), documento.getTipoMime());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        DocumentoSoporte documento = obtener(id);
        eliminarEntidad(documento);
    }


    @Override
    @Transactional
    public void eliminarPorIngreso(Integer idIngreso) {
        documentoRepository.findByIngreso_IdOrderByFechaCargaDesc(idIngreso).forEach(this::eliminarEntidad);
    }

    @Override
    @Transactional
    public void eliminarPorEgreso(Integer idEgreso) {
        documentoRepository.findByEgreso_IdOrderByFechaCargaDesc(idEgreso).forEach(this::eliminarEntidad);
    }

    private void eliminarEntidad(DocumentoSoporte documento) {
        documentoRepository.delete(documento);
        storageService.eliminar(documento.getStorageKey());
    }

    private DocumentoSoporte crearDocumento(TipoDocumentoSoporte tipoDocumento, String numeroDocumento,
                                              LocalDate fechaDocumento, String nombreTercero,
                                              String identificacionTercero, MultipartFile archivo) {
        if (tipoDocumento == null) {
            throw new BadRequestException("Debe indicar el tipo de documento soporte.");
        }
        DocumentoSoporte documento = new DocumentoSoporte();
        documento.setTipoDocumento(tipoDocumento);
        documento.setNumeroDocumento(normalizar(numeroDocumento, 80, "El número de documento"));
        documento.setFechaDocumento(fechaDocumento);
        documento.setNombreTercero(normalizar(nombreTercero, 150, "El nombre del tercero"));
        documento.setIdentificacionTercero(normalizar(identificacionTercero, 40, "La identificación del tercero"));
        documento.setStorageKey(storageService.guardar(archivo));
        documento.setNombreArchivoOriginal(archivo.getOriginalFilename() == null ? "documento" : archivo.getOriginalFilename());
        documento.setTipoMime(archivo.getContentType() == null ? "application/octet-stream" : archivo.getContentType());
        documento.setTamanoBytes(archivo.getSize());
        documento.setFechaCarga(LocalDateTime.now());
        return documento;
    }

    private DocumentoSoporte obtener(Long id) {
        if (id == null) throw new BadRequestException("El ID del documento es obligatorio.");
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El documento soporte solicitado no existe."));
    }

    private DocumentoSoporteDTO convertir(DocumentoSoporte entidad) {
        DocumentoSoporteDTO dto = new DocumentoSoporteDTO();
        dto.setId(entidad.getId());
        if (entidad.getIngreso() != null) {
            dto.setOrigen("INGRESO");
            dto.setIdMovimiento(entidad.getIngreso().getId());
        } else if (entidad.getEgreso() != null) {
            dto.setOrigen("EGRESO");
            dto.setIdMovimiento(entidad.getEgreso().getId());
        }
        dto.setTipoDocumento(entidad.getTipoDocumento());
        dto.setNumeroDocumento(entidad.getNumeroDocumento());
        dto.setFechaDocumento(entidad.getFechaDocumento());
        dto.setNombreTercero(entidad.getNombreTercero());
        dto.setIdentificacionTercero(entidad.getIdentificacionTercero());
        dto.setNombreArchivo(entidad.getNombreArchivoOriginal());
        dto.setTipoMime(entidad.getTipoMime());
        dto.setTamanoBytes(entidad.getTamanoBytes());
        dto.setFechaCarga(entidad.getFechaCarga());
        return dto;
    }

    private String normalizar(String valor, int max, String etiqueta) {
        if (valor == null || valor.isBlank()) return null;
        String limpio = valor.trim();
        if (limpio.length() > max) throw new BadRequestException(etiqueta + " no puede superar " + max + " caracteres.");
        return limpio;
    }
}
