package ProgressoApp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

  @NotBlank(message = "Nie podano adresu e-mail")
  @Email(message = "Niepoprawny format adresu e-mail")
  private String email;
  @NotNull(message = "Nie podano hasła")
  @Size(min = 6, max = 25, message = "Hasło musi składać się z przynajmniej {min} znaków i nie przekraczać {max}")
  private String password;
}
