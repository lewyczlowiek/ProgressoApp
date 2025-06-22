package ProgressoApp.task;

import ProgressoApp.controllers.api.TaskController;
import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.response.TaskResponseDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.TaskSubmissionService;
import ProgressoApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerUnitTest {

  private MockMvc mockMvc;

  @Mock
  private TaskService taskService;

  @Mock
  private ProjectService projectService;

  @Mock
  private UserService userService;

  @Mock
  private TaskSubmissionService taskSubmissionService;

  @InjectMocks
  private TaskController taskController;

  private Task sampleTask;

  @BeforeEach
  void setup() {
    sampleTask = new Task();

    mockMvc = MockMvcBuilders.standaloneSetup(taskController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }


  @Test
  void getAllTasks_shouldReturnPagedTasks() throws Exception {
    TaskResponseDTO taskResponseDTO = new TaskResponseDTO(
        sampleTask.getTaskId(),
        sampleTask.getName(),
        sampleTask.getDescription(),
        sampleTask.getTaskOrder() != null ? sampleTask.getTaskOrder().toString() : null,
        sampleTask.getTaskStatus(),
        sampleTask.getProject().getProjectId(),
        sampleTask.getCreationTimestamp(),
        sampleTask.getDueDate(),
        List.of()
    );

    Page<TaskResponseDTO> page = new PageImpl<>(List.of(taskResponseDTO), PageRequest.of(0, 10), 1);
    // Tutaj ważne jest użycie any(Pageable.class)
    given(taskService.getAllTasks(any(Pageable.class))).willReturn(page);

    mockMvc.perform(get("/api/tasks")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Test Task"));
  }

  @Test
  void getTaskById_shouldReturnTask() throws Exception {
    given(taskService.findById(1L)).willReturn(sampleTask);

    mockMvc.perform(get("/api/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Task"));
  }

  @Test
  void updateTask_shouldReturnUpdatedTask() throws Exception {
    TaskRequestDTO dto = new TaskRequestDTO("Updated", "Updated Desc", 2, TaskStatus.IN_PROGRESS,
        1L, LocalDate.now());
    sampleTask.setName(dto.name());
    sampleTask.setDescription(dto.description());
    sampleTask.setTaskOrder(dto.taskOrder());
    sampleTask.setTaskStatus(dto.taskStatus());
    sampleTask.setDueDate(dto.dueDate());

    given(taskService.updateTask(eq(1L), any(TaskRequestDTO.class))).willReturn(sampleTask);

    String json = """
        {
          "name": "Updated",
          "description": "Updated Desc",
          "taskOrder": 2,
          "taskStatus": "IN_PROGRESS",
          "projectId": 1,
          "dueDate": "%s"
        }
        """.formatted(dto.dueDate());

    mockMvc.perform(put("/api/tasks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"))
        .andExpect(jsonPath("$.taskStatus").value("IN_PROGRESS"));
  }

  @Test
  void deleteTask_shouldReturnRedirect() throws Exception {
    mockMvc.perform(delete("/api/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("redirect:/tasks"));

    verify(taskService).deleteTask(1L);
  }
}
