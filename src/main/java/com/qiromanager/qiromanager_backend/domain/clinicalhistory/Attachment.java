package com.qiromanager.qiromanager_backend.domain.clinicalhistory;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@Table(name = "attachments")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String originalFilename;

    private String mimeType;

    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_record_id", nullable = false)
    private ClinicalRecord clinicalRecord;

    protected Attachment() {
    }

    private Attachment(String url, String originalFilename, String mimeType, Long size, ClinicalRecord clinicalRecord) {
        this.url = url;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.size = size;
        this.clinicalRecord = clinicalRecord;
    }

    public static Attachment create(String url, String originalFilename, String mimeType, Long size, ClinicalRecord clinicalRecord) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Attachment URL cannot be empty");
        }
        if (clinicalRecord == null) {
            throw new IllegalArgumentException("Attachment must belong to a clinical record");
        }
        return new Attachment(url, originalFilename, mimeType, size, clinicalRecord);
    }
}