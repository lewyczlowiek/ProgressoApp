package ProgressoApp.config;

import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestExecutionListeners;

@Configuration
@Profile("test")
public class TestDataConfig {

  private final UserRepository userRepository;

  public TestDataConfig(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @BeforeEach
  public void setUpTestData() {
    // Dodajemy dane testowe, które będą ładowane na początku testów
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
  }
}
