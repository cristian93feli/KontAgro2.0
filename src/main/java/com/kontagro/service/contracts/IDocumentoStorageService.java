package com.kontagro.service.contracts;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IDocumentoStorageService {
    String guardar(MultipartFile archivo);
    Resource cargar(String storageKey);
    void eliminar(String storageKey);
}
