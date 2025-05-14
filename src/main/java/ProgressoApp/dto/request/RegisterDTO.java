package ProgressoApp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {


  private Long id;
  @NotEmpty(message = "Imię nie może być puste!")
  private String firstName;

  @NotEmpty(message = "Nazwisko nie może być puste!")
  private String lastName;

  @NotEmpty(message = "Numer indeksu nie może być pusty!")
  private String numberIndex;

  @Email(message = "Niepoprawny format adresu e-mail")
  @NotEmpty(message = "Email jest wymagany")
  private String email;

  @NotEmpty(message = "Hasło nie może być puste!")
  private String password;

  public RegisterDTO(String firstName, String lastName, String numberIndex, String email,
      String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.numberIndex = numberIndex;
    this.email = email;
    this.password = password;
  }

  public RegisterDTO() {
  }
}
