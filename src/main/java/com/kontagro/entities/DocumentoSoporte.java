package com.kontagro.entities;

import com.kontagro.entities.enums.TipoDocumentoSoporte;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "documentos_soporte", indexes = {
        @Index(name = "idx_documento_ingreso", columnList = "id_ingreso"),
        @Index(name = "idx_documento_egreso", columnList = "id_egreso")
})
public class DocumentoSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento_soporte")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ingreso")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Ingreso ingreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_egreso")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Egreso egreso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 30)
    private TipoDocumentoSoporte tipoDocumento;

    @Column(name = "numero_documento", length = 80)
    private String numeroDocumento;

    @Column(name = "fecha_documento")
    private LocalDate fechaDocumento;

    @Column(name = "nombre_tercero", length = 150)
    private String nombreTercero;

    @Column(name = "identificacion_tercero", length = 40)
    private String identificacionTercero;

    @Column(name = "nombre_archivo_original", nullable = false, length = 255)
    private String nombreArchivoOriginal;

    @Column(name = "tipo_mime", nullable = false, length = 100)
    private String tipoMime;

    @Column(name = "tamano_bytes", nullable = false)
    private long tamanoBytes;

    @Column(name = "storage_key", nullable = false, unique = true, length = 255)
    private String storageKey;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga;
}
