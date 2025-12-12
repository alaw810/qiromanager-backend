package com.qiromanager.qiromanager_backend.api.users;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {

    @NotNull
    private Boolean active;
}
