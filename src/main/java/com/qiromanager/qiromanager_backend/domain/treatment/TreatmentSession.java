package com.qiromanager.qiromanager_backend.domain.treatment;

import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_sessions")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TreatmentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private User therapist;

    @Column(nullable = false)
    private LocalDateTime sessionDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected TreatmentSession() {
    }

    private TreatmentSession(Patient patient, User therapist, LocalDateTime sessionDate, String notes) {
        this.patient = patient;
        this.therapist = therapist;
        this.sessionDate = sessionDate;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static TreatmentSession create(Patient patient, User therapist, LocalDateTime sessionDate, String notes) {
        if (patient == null) throw new IllegalArgumentException("Patient is required");
        if (therapist == null) throw new IllegalArgumentException("Therapist is required");
        if (sessionDate == null) throw new IllegalArgumentException("Session date is required");

        return new TreatmentSession(patient, therapist, sessionDate, notes);
    }

    public void updateNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
}