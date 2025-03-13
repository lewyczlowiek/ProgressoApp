package ProgressoApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long Id;
    private String firstname;
    private String lastname;
    private Long indexNumber;

    @Column(unique = true)
    private String email;

    @NonNull
    private String password;
    /**
     * TO DO  poprawne logowanie na bazie informacji o użytkowniku:
     * - imie
     * - nazwisko
     * - e-mail
     * - Rola (student/wykładowca)
     * - Numer Indeksu
     *
     * Obsłużyć to wraz z UserDetails, narazie szkielet dla bazy wstępnego ustalenia
     */
}
