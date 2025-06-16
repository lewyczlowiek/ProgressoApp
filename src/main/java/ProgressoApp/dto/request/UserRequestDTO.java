package ProgressoApp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record UserRequestDTO(

        @NotEmpty String firstName,
        @NotEmpty String lastName,
        @NotEmpty String numberIndex,
        @Email String email,
        @NotEmpty String password
) {}
