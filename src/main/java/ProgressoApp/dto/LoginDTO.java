package ProgressoApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @Email(message = "Podaj poprawny e-mail!")
    @NotEmpty(message = "E-mail jest wymagany!")
    private String email;

    @NotEmpty(message = "Hasło nie może być puste!!!")
    private String password;
}
