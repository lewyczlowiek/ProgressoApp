package ProgressoApp.dto.request;

import ProgressoApp.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserRequestDTO(

    @NotEmpty String firstName,
    @NotEmpty String lastName,
    @NotEmpty String numberIndex,
    @Email String email,
    @NotEmpty String password,
    @NotNull Role role
) {

}
