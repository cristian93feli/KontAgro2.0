package com.kontagro.service.implementation;

import com.kontagro.exceptions.BadRequestException;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.service.contracts.IDocumentoStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentoStorageServiceImpl implements IDocumentoStorageService {

    private static final long MAX_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> MIME_PERMITIDOS = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/xml",
            "text/xml"
    );
    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("pdf", "jpg", "jpeg", "png", "xml");

    private final Path storageRoot;

    public DocumentoStorageServiceImpl(@Value("${kontagro.documentos.ruta:./data/documentos}") String ruta) {
        try {
            this.storageRoot = Path.of(ruta).toAbsolutePath().normalize();
            Files.createDirectories(storageRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible inicializar el almacenamiento de documentos.", ex);
        }
    }

    @Override
    public String guardar(MultipartFile archivo) {
        validarArchivo(archivo);
        String original = archivo.getOriginalFilename() == null ? "documento" : archivo.getOriginalFilename();
        String extension = obtenerExtension(original);
        String storageKey = UUID.randomUUID() + "." + extension;
        Path destino = resolverSeguro(storageKey);

        try (InputStream input = archivo.getInputStream()) {
            Files.copy(input, destino, StandardCopyOption.REPLACE_EXISTING);
            return storageKey;
        } catch (IOException ex) {
            throw new BadRequestException("No fue posible almacenar el documento adjunto. Intente nuevamente.");
        }
    }

    @Override
    public Resource cargar(String storageKey) {
        Path archivo = resolverSeguro(storageKey);
        try {
            Resource resource = new UrlResource(archivo.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("El archivo solicitado ya no está disponible en el almacenamiento.");
            }
            return resource;
        } catch (java.net.MalformedURLException ex) {
            throw new ResourceNotFoundException("No fue posible localizar el archivo solicitado.");
        }
    }

    @Override
    public void eliminar(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) return;
        try {
            Files.deleteIfExists(resolverSeguro(storageKey));
        } catch (IOException ignored) {
            // La eliminación física no debe impedir conservar la consistencia de metadatos.
        }
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("Debe seleccionar un documento para cargar.");
        }
        if (archivo.getSize() > MAX_BYTES) {
            throw new BadRequestException("El documento no puede superar los 10 MB.");
        }

        String original = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename();
        String extension = obtenerExtension(original);
        String mime = archivo.getContentType() == null ? "" : archivo.getContentType().toLowerCase(Locale.ROOT);

        if (!EXTENSIONES_PERMITIDAS.contains(extension) || !MIME_PERMITIDOS.contains(mime)) {
            throw new BadRequestException("Formato de documento no permitido. Use PDF, JPG, PNG o XML.");
        }
    }

    private String obtenerExtension(String nombre) {
        int posicion = nombre.lastIndexOf('.');
        if (posicion < 0 || posicion == nombre.length() - 1) return "";
        return nombre.substring(posicion + 1).toLowerCase(Locale.ROOT);
    }

    private Path resolverSeguro(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            throw new ResourceNotFoundException("El identificador del archivo no es válido.");
        }
        Path resuelto = storageRoot.resolve(storageKey).normalize();
        if (!resuelto.startsWith(storageRoot)) {
            throw new BadRequestException("La ruta del documento no es válida.");
        }
        return resuelto;
    }
}
