package ProgressoApp.task;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private ProjectService projectService;

  @InjectMocks
  private TaskService taskService;

  private TaskRequestDTO taskDTO;
  private Project mockProject;

  @BeforeEach
  void setUp() {
    mockProject = new Project();
    mockProject.setProjectId(1L);

    taskDTO = new TaskRequestDTO(
        "Test Task",
        "Test Description",
        1,
        TaskStatus.TO_DO,
        1L,
        LocalDate.of(2024, 12, 31)
    );
  }

  @Test
  void createTask_shouldSaveTaskWithProject() {
    Task taskToSave = new Task(taskDTO);
    taskToSave.setProject(mockProject);

    when(projectService.findById(1L)).thenReturn(mockProject);
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Task result = taskService.createTask(taskDTO);

    assertThat(result.getName()).isEqualTo("Test Task");
    assertThat(result.getProject()).isEqualTo(mockProject);
    verify(taskRepository).save(any(Task.class));
  }

  @Test
  void findById_shouldReturnTaskIfExists() {
    Task task = new Task(taskDTO);
    task.setTaskId(123L);

    when(taskRepository.findById(123L)).thenReturn(Optional.of(task));

    Task result = taskService.findById(123L);

    assertThat(result.getTaskId()).isEqualTo(123L);
    assertThat(result.getName()).isEqualTo("Test Task");
  }

  @Test
  void updateStatus_shouldUpdateAndSaveTask() {
    Task task = new Task(taskDTO);
    task.setTaskId(1L);
    task.setTaskStatus(TaskStatus.TO_DO);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Task updated = taskService.updateStatus(1L, TaskStatus.DONE);

    assertThat(updated.getTaskStatus()).isEqualTo(TaskStatus.DONE);
  }

  @Test
  void updateTask_shouldModifyFieldsAndSave() {
    Task existing = new Task(taskDTO);
    existing.setTaskId(1L);

    TaskRequestDTO updatedDTO = new TaskRequestDTO(
        "Nowa nazwa",
        "Nowy opis",
        10,
        TaskStatus.DONE,
        1L,
        LocalDate.of(2025, 1, 1)
    );

    when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(projectService.findById(1L)).thenReturn(mockProject);
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Task updated = taskService.updateTask(1L, updatedDTO);

    assertThat(updated.getName()).isEqualTo("Nowa nazwa");
    assertThat(updated.getTaskOrder()).isEqualTo(10);
    assertThat(updated.getTaskStatus()).isEqualTo(TaskStatus.DONE);
    assertThat(updated.getDueDate()).isEqualTo(LocalDate.of(2025, 1, 1));
  }

  @Test
  void deleteTask_shouldRemoveIfExists() {
    Task task = new Task(taskDTO);
    task.setTaskId(1L);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    taskService.deleteTask(1L);

    verify(taskRepository).delete(task);
  }

  @Test
  void findAll_shouldReturnList() {
    List<Task> tasks = List.of(new Task(), new Task());

    when(taskRepository.findAll()).thenReturn(tasks);

    List<Task> result = taskService.findAll();

    assertThat(result).hasSize(2);
    verify(taskRepository).findAll();
  }
}
