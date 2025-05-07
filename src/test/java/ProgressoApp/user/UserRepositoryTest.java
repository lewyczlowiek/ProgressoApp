package ProgressoApp.user;

import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void shouldFindUserByEmail() {
    // Dodanie użytkowników testowych bezpośrednio w teście
    User admin = User.builder()
        .firstName("Admin")
        .lastName("User")
        .email("admin@test.com")
        .password("admin1234")
        .role(Role.ADMIN)
        .numberIndex("0001")
        .build();

    User student = User.builder()
        .firstName("Student")
        .lastName("One")
        .email("student@test.com")
        .password("student123")
        .role(Role.STUDENT)
        .numberIndex("0002")
        .build();

    userRepository.save(admin);
    userRepository.save(student);

    // Testowanie wyszukiwania użytkownika po e-mailu
    Optional<User> userOpt = userRepository.findByEmail("admin@test.com");

    assertThat(userOpt).isPresent();
    assertThat(userOpt.get().getFirstName()).isEqualTo("Admin");
  }
}
