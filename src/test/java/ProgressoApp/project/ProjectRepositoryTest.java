package ProgressoApp.project;

import ProgressoApp.model.Project;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class ProjectRepositoryTest {

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private UserRepository userRepository;

  private Project project;
  private User user;

  @BeforeEach
  void setUp() {
    projectRepository.deleteAll();
    userRepository.deleteAll();

    user = new User();
    user.setFirstName("Adam");
    user.setLastName("Małysz");
    user.setNumberIndex("20");
    user.setEmail("thekriso@wp.pl");
    user.setPassword("abcdhijklml");
    user.setRole(Role.STUDENT);
    user = userRepository.save(user);

    project = new Project();
    project.setName("Spring Boot App");
    project.setDescription("A simple Spring Boot project");
    project.setCreationTimestamp(LocalDateTime.now());
    project.setEndDateTime(LocalDateTime.now().plusDays(5));
    project.setStatusProject("active");
    project.setUsers(Set.of(user));  // ważne: user musi być zapisany
    project = projectRepository.save(project);
  }

  @Test
  void shouldFindByProjectId() {
    Optional<Project> found = projectRepository.findByProjectId(project.getProjectId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Spring Boot App");
  }

  @Test
  void shouldFindByNameContainingIgnoreCase() {
    Page<Project> page = projectRepository.findByNameContainingIgnoreCase("spring",
        PageRequest.of(0, 10));
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent().get(0).getName()).containsIgnoringCase("spring");
  }

  @Test
  void shouldFindByUserEmail() {
    Page<Project> page = projectRepository.findByUserEmail("thekriso@wp.pl", PageRequest.of(0, 10));
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent().get(0).getUsers()).contains(user);
  }

  @Test
  void shouldFindByUserEmailAndNameContaining() {
    Page<Project> page = projectRepository.findByUserEmailAndNameContaining(
        "thekriso@wp.pl", "boot", PageRequest.of(0, 10));
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent().get(0).getName()).containsIgnoringCase("boot");
  }

  @Test
  void shouldReturnEmptyPageIfUserNotMatch() {
    Page<Project> page = projectRepository.findByUserEmailAndNameContaining(
        "nonexistent@email.com", "boot", PageRequest.of(0, 10));
    assertThat(page.getContent()).isEmpty();
  }
}
