package com.qiromanager.qiromanager_backend.api.patients;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePatientRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String phone;

    @Email(message = "Email must be valid")
    private String email;

    private String address;

    private String generalNotes;
}
