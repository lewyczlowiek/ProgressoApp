package ProgressoApp.model;

import ProgressoApp.dto.response.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  private String firstName;
  private String lastName;

  @Column(unique = true, nullable = false)
  private String numberIndex;

  @NotEmpty(message = "Nie podano adresu e-mail")
  @Email(message = "Niepoprawny format adresu e-mail")
  @Column(unique = true, nullable = false)
  private String email;

  @JsonIgnore
  @Size(min = 8, max = 64, message = "Hasło musi składać się z przynajmniej {min} i nie przekraczać {max} znaków")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TaskSubmission> submissions;

  public User(User user) {
    this.email = user.email;
    this.role = user.role;
    this.firstName = user.firstName;
    this.lastName = user.lastName;
    this.numberIndex = user.numberIndex;
    this.submissions = user.submissions;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
  }

  public UserResponseDTO toUserResponseDTO() {
    return new UserResponseDTO(
        this.getUserId(),
        this.getFirstName(),
        this.getLastName(),
        this.getEmail(),
        this.getRole(),
        this.getNumberIndex(),
        this.getSubmissions() != null ? this.submissions.stream()
            .map(TaskSubmission::toTaskSubmissionResponseDTO).toList() : List.of()

    );
  }

  @Override
  public String getUsername() {
    return email;
  }
}
