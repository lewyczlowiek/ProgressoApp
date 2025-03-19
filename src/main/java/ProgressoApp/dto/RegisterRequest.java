package ProgressoApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Imię nie może być puste!")
    private String firstName;

    @NotBlank(message = "Nazwisko nie może być puste!")
    private String lastName;

    @NotBlank(message = "Numer indeksu nie może być pusty!")
    private String numberIndex;

    @Email(message = "Podaj poprawny email")
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @NotBlank(message = "Hasło nie może być puste!")
    private String password;

}
