package ProgressoApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Imię nie może być puste!")
    private String imie;

    @NotBlank(message = "Nazwisko nie może być puste!")
    private String nazwisko;

    @NotBlank(message = "Numer indeksu nie może być pusty!")
    private String numerIndeksu;

    @Email(message = "Podaj poprawny email")
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @NotBlank(message = "Hasło nie może być puste!")
    private String password;

    // Getter and Setter for imie
    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    // Getter and Setter for nazwisko
    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    // Getter and Setter for numerIndeksu
    public String getNumerIndeksu() {
        return numerIndeksu;
    }

    public void setNumerIndeksu(String numerIndeksu) {
        this.numerIndeksu = numerIndeksu;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
