package ProgressoApp.user;

import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");
  @Autowired
  JdbcConnectionDetails jdbcConnectionDetails;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    User user = new User(null, "Krzysztof", "Olejniczak", "456718", "thekriso@wp.pl", "haslo12345",
        Role.ADMIN);
    userRepository.save(user);
  }

  @Test
  void connectionEstablished() {
    assertThat(postgres.isCreated()).isTrue();
    assertThat(postgres.isRunning()).isTrue();
  }

  @Test
  void shouldSaveAndFindUser() {

    List<User> allUsers = userRepository.findAll();
    assertThat(allUsers).hasSize(1);
    assertThat(allUsers.get(0).getEmail()).isEqualTo("thekriso@wp.pl");
  }
}
