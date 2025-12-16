package com.qiromanager.qiromanager_backend.domain.patient;

import com.qiromanager.qiromanager_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private LocalDate dateOfBirth;

    private String phone;

    private String email;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String generalNotes;

    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_therapists",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "therapist_id")
    )
    private Set<User> therapists = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Patient() {
    }

    private Patient(String fullName, LocalDate dateOfBirth, String phone, String email, String address, String generalNotes) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.generalNotes = generalNotes;
        this.active = true;
        this.therapists = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Patient create(String fullName, LocalDate dateOfBirth, String phone, String email, String address, String generalNotes) {
        return new Patient(fullName, dateOfBirth, phone, email, address, generalNotes);
    }

    public void update(String fullName, LocalDate dateOfBirth, String phone, String email, String address, String generalNotes) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.generalNotes = generalNotes;
    }
    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void assignTherapist(User therapist) {
        this.therapists.add(therapist);
    }

    public void unassignTherapist(User therapist) {
        this.therapists.removeIf(t -> t.equals(therapist));
    }

    public Set<User> getTherapists() {
        return Collections.unmodifiableSet(therapists);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
