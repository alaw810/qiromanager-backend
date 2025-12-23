package com.qiromanager.qiromanager_backend.domain.clinicalhistory;

import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clinical_records")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "clinicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attachment> attachments = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected ClinicalRecord() {
    }

    private ClinicalRecord(Patient patient, User performedBy, RecordType type, String content) {
        this.patient = patient;
        this.performedBy = performedBy;
        this.type = type;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static ClinicalRecord create(Patient patient, User performedBy, RecordType type, String content) {
        if (patient == null) {
            throw new IllegalArgumentException("Clinical record must be assigned to a patient");
        }
        if (type == null) {
            throw new IllegalArgumentException("Record type is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        return new ClinicalRecord(patient, performedBy, type, content);
    }

    public void update(String content, RecordType type) {
        boolean changed = false;

        if (content != null && !content.isBlank() && !content.equals(this.content)) {
            this.content = content;
            changed = true;
        }

        if (type != null && this.type != type) {
            this.type = type;
            changed = true;
        }

        if (changed) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addAttachment(String url, String publicId, String originalFilename, String mimeType, Long size) {
        Attachment attachment = Attachment.create(url, publicId, originalFilename, mimeType, size, this);
        this.attachments.add(attachment);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeAttachment(Attachment attachment) {
        if (this.attachments.remove(attachment)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public Set<Attachment> getAttachments() {
        return Collections.unmodifiableSet(attachments);
    }
}