package ProgressoApp.task;

import ProgressoApp.controllers.view.TaskViewController;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskViewControllerTest {

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
  private TaskViewController taskViewController;

  private Task sampleTask;
  private Project sampleProject;

  @BeforeEach
  void setUp() {
    sampleProject = new Project();
    sampleProject.setProjectId(1L);
    sampleProject.setName("Sample Project");
    sampleProject.setUsers(new HashSet<>());

    sampleTask = new Task();
    sampleTask.setTaskId(1L);
    sampleTask.setName("Test Task");
    sampleTask.setDescription("Task Description");
    sampleTask.setTaskStatus(TaskStatus.TO_DO);
    sampleTask.setCreationTimestamp(LocalDateTime.now());
    sampleTask.setDueDate(LocalDate.now().plusDays(3));
    sampleTask.setProject(sampleProject);

    mockMvc = MockMvcBuilders.standaloneSetup(taskViewController).build();
  }

  @Test
  void showTaskDetails_shouldReturnTaskDetailsView() throws Exception {
    when(taskService.findById(1L)).thenReturn(sampleTask);

    mockMvc.perform(get("/tasks/details/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("task_details"))
        .andExpect(model().attributeExists("task"))
        .andExpect(model().attribute("task", sampleTask));
  }

  @Test
  void deleteTask_shouldRedirectToTaskList() throws Exception {
    mockMvc.perform(post("/tasks/1/delete"))
        .andExpect(status().isOk())
        .andExpect(view().name("task_get"));

    verify(taskService).deleteTask(1L);
  }

  @Test
  void showEditForm_shouldReturnEditViewWithData() throws Exception {
    when(taskService.findById(1L)).thenReturn(sampleTask);
    when(projectService.getAllProjects(any())).thenReturn(
        new org.springframework.data.domain.PageImpl<>(List.of()));

    mockMvc.perform(get("/tasks/1/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("task_edit"))
        .andExpect(model().attributeExists("task", "projects", "assignedUsers"));
  }

  @Test
  void showAddTaskForm_shouldReturnFormWithProjectUsers() throws Exception {
    when(projectService.findById(1L)).thenReturn(sampleProject);

    mockMvc.perform(get("/tasks/addTasks/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("task_file"))
        .andExpect(model().attributeExists("task", "project", "assignedUsers"));
  }

  @Test
  void addTask_shouldRedirectToProjectDetails() throws Exception {
    Task createdTask = new Task();
    createdTask.setTaskId(10L);
    when(taskService.createTask(any())).thenReturn(createdTask);

    mockMvc.perform(post("/tasks/addTasks/1")
            .param("name", "New Task")
            .param("description", "Desc")
            .param("dueDate", "2025-07-01"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/project/details/1"));

    verify(taskService).createTask(any());
  }
}
