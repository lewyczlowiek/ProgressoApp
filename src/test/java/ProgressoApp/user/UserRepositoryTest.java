package ProgressoApp.user;

import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    User user1 = User.builder()
        .firstName("Krzysztof")
        .lastName("Olejniczak")
        .email("krzysztof@example.com")
        .password("haslo12345")
        .numberIndex("456718")
        .role(Role.ADMIN)
        .build();

    User user2 = User.builder()
        .firstName("Anna")
        .lastName("Nowak")
        .email("anna.nowak@example.com")
        .password("password123")
        .numberIndex("123456")
        .role(Role.STUDENT)
        .build();

    User user3 = User.builder()
        .firstName("Jan")
        .lastName("Kowalski")
        .email("jan.kowalski@example.com")
        .password("pass123456")
        .numberIndex("789012")
        .role(Role.STUDENT)
        .build();

    userRepository.saveAll(List.of(user1, user2, user3));
  }

  @Test
  void shouldFindUserByNumberIndex() {
    Optional<User> userOpt = userRepository.findByNumberIndex("123456");
    assertThat(userOpt).isPresent();
    assertThat(userOpt.get().getFirstName()).isEqualTo("Anna");
  }

  @Test
  void shouldReturnEmptyOptionalIfNumberIndexNotFound() {
    Optional<User> userOpt = userRepository.findByNumberIndex("000000");
    assertThat(userOpt).isEmpty();
  }

  @Test
  void shouldCheckExistsByEmailAndNumberIndex() {
    assertThat(userRepository.existsByEmail("anna.nowak@example.com")).isTrue();
    assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();

    assertThat(userRepository.existsByNumberIndex("789012")).isTrue();
    assertThat(userRepository.existsByNumberIndex("000000")).isFalse();
  }

  @Test
  void shouldFindByEmailContainingIgnoreCase() {
    Pageable pageable = PageRequest.of(0, 10);

    Page<User> page = userRepository.findByEmailContainingIgnoreCase("Example", pageable);
    assertThat(page.getTotalElements()).isEqualTo(3);

    Page<User> page2 = userRepository.findByEmailContainingIgnoreCase("anna", pageable);
    assertThat(page2.getTotalElements()).isEqualTo(1);
    assertThat(page2.getContent().get(0).getFirstName()).isEqualTo("Anna");

    Page<User> page3 = userRepository.findByEmailContainingIgnoreCase("nonexistent", pageable);
    assertThat(page3.getTotalElements()).isEqualTo(0);
  }

  @Test
  void shouldFindByEmail() {
    Optional<User> userOpt = userRepository.findByEmail("jan.kowalski@example.com");
    assertThat(userOpt).isPresent();
    assertThat(userOpt.get().getLastName()).isEqualTo("Kowalski");
  }

  @Test
  void shouldReturnEmptyOptionalIfEmailNotFound() {
    Optional<User> userOpt = userRepository.findByEmail("noone@example.com");
    assertThat(userOpt).isEmpty();
  }

  @Test
  void shouldReturnAllUsers() {
    List<User> users = userRepository.findAll();
    assertThat(users).hasSize(3);
  }
}
