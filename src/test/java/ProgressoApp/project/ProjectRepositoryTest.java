package ProgressoApp.project;

import ProgressoApp.model.Project;
import ProgressoApp.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ProjectRepositoryTest {

  @Autowired
  private ProjectRepository projectRepository;

  @Test
  void shouldSaveProject() {
    Project project = new Project("Test Description", "Test Project",
        LocalDateTime.now(), LocalDate.now().plusDays(3));

    Project saved = projectRepository.save(project);

    assertThat(saved.getProjectId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Test Project");
  }
}
