package ProgressoApp.project;

import ProgressoApp.controllers.view.ProjectViewController;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectViewController.class)
@Import(ProjectControllerTest.TestConfig.class)
public class ProjectControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProjectService projectService;  // mock z TestConfig

  private Project sampleProject;
  private User sampleUser;
  private Task taskTodo, taskInProgress, taskDone;

  @BeforeEach
  void setup() {
    sampleUser = new User();
    sampleUser.setUserId(1L);
    sampleUser.setFirstName("John");
    sampleUser.setLastName("Doe");

    taskTodo = new Task();
    taskTodo.setTaskStatus(TaskStatus.TO_DO);
    taskInProgress = new Task();
    taskInProgress.setTaskStatus(TaskStatus.IN_PROGRESS);
    taskDone = new Task();
    taskDone.setTaskStatus(TaskStatus.DONE);

    sampleProject = new Project();
    sampleProject.setProjectId(1L);
    sampleProject.setName("Sample Project");
    sampleProject.setDescription("Project description");
    sampleProject.setEndDateTime(LocalDateTime.now().plusDays(10));
    sampleProject.setStatusProject("active");
    sampleProject.setTasks(List.of(taskTodo, taskInProgress, taskDone));
    sampleProject.setUsers(Set.of(sampleUser));

    Mockito.reset(projectService);
    when(projectService.findById(1L)).thenReturn(sampleProject);
  }

  @Test
  @WithMockUser(roles = {"USER"})
  void showEditProjectForm_shouldReturnEditForm() throws Exception {
    mockMvc.perform(get("/project/edit/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("edit_project"))
        .andExpect(model().attributeExists("project"))
        .andExpect(model().attributeExists("formattedEndDateTime"));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  void showAddProjectForm_shouldReturnForm() throws Exception {
    mockMvc.perform(get("/project/add"))
        .andExpect(status().isOk())
        .andExpect(view().name("project_file"))
        .andExpect(model().attributeExists("project"));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  void createProject_shouldRedirect() throws Exception {
    mockMvc.perform(post("/project/add")
            .param("name", "Test Project")
            .param("description", "Test Description")
            .param("statusProject", "active")
            .param("creationTimestamp", "2025-06-23T14:01:18")
            .param("endDateTime", "2025-06-28T14:01:18")
            .with(csrf()))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser(roles = {"USER"})
  void showProjectDetails_shouldReturnDetailsView() throws Exception {
    // Używamy wstrzykniętego mocka bezpośrednio
    when(projectService.findById(1L)).thenReturn(sampleProject);

    mockMvc.perform(get("/project/details/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("details_project"))
        .andExpect(model().attributeExists("project"))
        .andExpect(model().attributeExists("users"))
        .andExpect(model().attributeExists("todoTasks"))
        .andExpect(model().attributeExists("inProgressTasks"))
        .andExpect(model().attributeExists("doneTasks"));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  void deleteProject_shouldRedirect() throws Exception {
    mockMvc.perform(get("/project/delete/1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/index"));
  }

  // Usunięta metoda projectService(), niepotrzebna

  @Configuration
  static class TestConfig {

    @Bean
    public ProjectService projectService() {
      return Mockito.mock(ProjectService.class);
    }

    @Bean
    public UserService userService() {
      return Mockito.mock(UserService.class);
    }

    @Bean
    public TaskService taskService() {
      return Mockito.mock(TaskService.class);
    }

    @Bean
    public ProjectRepository projectRepository() {
      return Mockito.mock(ProjectRepository.class);
    }

    @Bean
    public UserRepository userRepository() {
      return Mockito.mock(UserRepository.class);
    }

    @Bean
    public ProjectViewController projectViewController(ProjectService projectService,
        UserService userService,
        TaskService taskService,
        ProjectRepository projectRepository,
        UserRepository userRepository) {
      return new ProjectViewController(projectService, userService, taskService, projectRepository,
          userRepository);
    }
  }
}
