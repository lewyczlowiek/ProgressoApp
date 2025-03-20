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
public class RegisterDTO {


  private Long id;
  @NotEmpty(message = "Imię nie może być puste!")
  private String firstName;

  @NotEmpty(message = "Nazwisko nie może być puste!")
  private String lastName;

  @NotEmpty(message = "Numer indeksu nie może być pusty!")
  private String numberIndex;

  @Email(message = "Podaj poprawny email")
  @NotEmpty(message = "Email jest wymagany")
  private String email;

  @NotEmpty(message = "Hasło nie może być puste!")
  private String password;

}
