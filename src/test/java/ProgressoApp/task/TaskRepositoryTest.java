package ProgressoApp.task;

import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Test
  void shouldSaveTaskWithProject() {
    Project project = new Project("Opis", "Zadanie Testowe", LocalDateTime.now(),
        LocalDate.now().plusDays(2));
    project = projectRepository.save(project);

    Task task = new Task("Zadanie", "Opis zadania", 1, LocalDateTime.now(), TaskStatus.TO_DO);
    task.setProject(project);

    Task saved = taskRepository.save(task);

    assertThat(saved.getTaskId()).isNotNull();
    assertThat(saved.getProject().getName()).isEqualTo("Zadanie Testowe");
  }
}
