package ProgressoApp.task;

import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // używa bazy H2
@ActiveProfiles("test")
class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ProjectRepository projectRepository;

  private Project project;

  @BeforeEach
  void setUp() {
    taskRepository.deleteAll();
    projectRepository.deleteAll();

    project = new Project();
    project.setName("Test Project");
    project = projectRepository.save(project);

    Task task = new Task();
    task.setName("Test Task");
    task.setDescription("Test Description");
    task.setTaskOrder(1);
    task.setTaskStatus(TaskStatus.TO_DO);
    task.setDueDate(LocalDate.of(2025, 6, 22));
    task.setProject(project);
    taskRepository.save(task);
  }

  @Test
  void shouldSaveAndFindTask() {
    List<Task> allTasks = taskRepository.findAll();
    assertThat(allTasks).hasSize(1);

    Task task = allTasks.get(0);
    assertThat(task.getName()).isEqualTo("Test Task");
    assertThat(task.getDescription()).isEqualTo("Test Description");
    assertThat(task.getTaskOrder()).isEqualTo(1);
    assertThat(task.getTaskStatus()).isEqualTo(TaskStatus.TO_DO);
    assertThat(task.getProject()).isEqualTo(project);
  }

  @Test
  void shouldFindTasksByProjectId() {
    List<Task> tasks = taskRepository.findByProject_ProjectId(project.getProjectId());
    assertThat(tasks).hasSize(1);
    assertThat(tasks.get(0).getName()).isEqualTo("Test Task");
  }

  @Test
  void shouldFindById() {
    Task savedTask = taskRepository.findAll().get(0);
    Optional<Task> foundTask = taskRepository.findById(savedTask.getTaskId());

    assertThat(foundTask).isPresent();
    assertThat(foundTask.get().getName()).isEqualTo("Test Task");
  }

  @Test
  void shouldDeleteTask() {
    Task savedTask = taskRepository.findAll().get(0);
    taskRepository.deleteById(savedTask.getTaskId());

    assertThat(taskRepository.findById(savedTask.getTaskId())).isEmpty();
  }
}
