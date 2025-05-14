package ProgressoApp.user;

import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {


  @Autowired
  private UserRepository userRepository;
  private UserService userService;

  @BeforeEach
  void setUp() {

    User user = User.builder()
        .firstName("Krzysztof")
        .lastName("Olejniczak")
        .email("thekriso@wp.pl")
        .password("haslo12345")
        .numberIndex("456718")
        .role(Role.ADMIN)
        .build();
    userRepository.save(user);
  }

  @Test
  void shouldSaveAndFindUser() {

    List<User> allUsers = userRepository.findAll();
    assertThat(allUsers).hasSize(1);
    assertThat(allUsers.get(0).getEmail()).isEqualTo("thekriso@wp.pl");
  }
}
