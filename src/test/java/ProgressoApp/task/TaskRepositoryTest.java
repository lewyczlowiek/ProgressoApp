package ProgressoApp.task;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.repository.TaskRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @BeforeEach
  void setUp() {
    TaskRequestDTO task = new TaskRequestDTO("dwa", "opis", 3, TaskStatus.TO_DO, 1,
        LocalDate.of(2024, 2, 20));
    taskRepository.save(task);
  }

  @Test
  void shouldSaveAndFindTask() {
    List<Task> allTasks = taskRepository.findAll();
    assertThat(allTasks).hasSize(1);
    assertThat(allTasks.get(0).getDescription()).isEqualTo("opis");
  }
}

