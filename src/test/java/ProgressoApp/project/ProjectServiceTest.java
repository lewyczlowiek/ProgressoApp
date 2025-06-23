package ProgressoApp.project;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Role;
import ProgressoApp.model.Task;
import ProgressoApp.model.User;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private ProjectService projectService;

  private Project existingProject;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User(1L, "Adam", "Małysz", "20", "thekriso@wp.pl", "abcd", Role.STUDENT,
        new ArrayList<>());

    existingProject = new Project();
    existingProject.setProjectId(1L);
    existingProject.setName("Old Project");
    existingProject.setDescription("Old Desc");
    existingProject.setEndDateTime(LocalDateTime.now().plusDays(2));
    existingProject.setStatusProject("inactive");
    existingProject.setUsers(new HashSet<>(List.of(user)));
    existingProject.setTasks(new ArrayList<>());
  }

  @Test
  void createProject_shouldSaveProject() {
    ProjectRequestDTO projectDTO = new ProjectRequestDTO(
        "Test Project",
        "Test Description",
        LocalDateTime.now().plusDays(5),
        List.of(),
        Set.of(user.toUserResponseDTO()),
        LocalDateTime.now().plusDays(5),
        "active"
    );

    when(projectRepository.save(any(Project.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    Project created = projectService.createProject(projectDTO);

    assertThat(created.getName()).isEqualTo("Test Project");
    assertThat(created.getDescription()).isEqualTo("Test Description");
    verify(projectRepository).save(any(Project.class));
  }

  @Test
  void findById_existingId_shouldReturnProject() {
    when(projectRepository.findByProjectId(1L)).thenReturn(Optional.of(existingProject));

    Project found = projectService.findById(1L);

    assertThat(found).isNotNull();
    assertThat(found.getProjectId()).isEqualTo(1L);
  }

  @Test
  void findById_nonExistingId_shouldThrow() {
    when(projectRepository.findByProjectId(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> projectService.findById(999L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Project not found");
  }

  @Test
  void updateProject_shouldModifyAndSave() {
    ProjectRequestDTO updateDTO = new ProjectRequestDTO(
        "Updated Project",
        "Updated Desc",
        LocalDateTime.now().plusDays(10),
        List.of(),
        Set.of(user.toUserResponseDTO()),
        LocalDateTime.now().plusDays(10),
        "active"
    );

    when(projectRepository.findByProjectId(1L)).thenReturn(Optional.of(existingProject));
    when(userService.findById(1L)).thenReturn(user);
    when(projectRepository.save(any(Project.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    Project updated = projectService.updateProject(1L, updateDTO);

    assertThat(updated.getName()).isEqualTo("Updated Project");
    assertThat(updated.getDescription()).isEqualTo("Updated Desc");
    assertThat(updated.getUsers()).contains(user);
    verify(projectRepository).save(existingProject);
  }

  @Test
  void deleteProject_existingId_shouldDelete() {
    when(projectRepository.findByProjectId(1L)).thenReturn(Optional.of(existingProject));

    projectService.deleteProject(1L);

    verify(projectRepository).delete(existingProject);
  }

  @Test
  void deleteProject_nonExisting_shouldThrow() {
    when(projectRepository.findByProjectId(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> projectService.deleteProject(999L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Project not found");
  }
}
