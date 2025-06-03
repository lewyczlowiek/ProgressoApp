package ProgressoApp.project;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class ProjectServiceTest {

  @Autowired
  private ProjectService projectService;


  private ProjectRequestDTO dto;
  private Project savedProject;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    dto = new ProjectRequestDTO(
        "Zadanie 1",
        "Napisać test",
        LocalDateTime.of(2024, 5, 1, 12, 0),
        new ArrayList<>(),
        new HashSet<>()
    );

    projectService.createProject(dto);

    savedProject = new Project(dto);
    savedProject.setProjectId(1L);


  }

//  @Test
//  void shouldSaveProject() {
//
//    projectService.createProject(dto);
//    List<Project> projects = projectService.getAllProjects();
//
//    assertThat(projects).hasSize(4);
//    assertThat(projects.get(0).getName()).isEqualTo("Zadanie 1");
//    assertThat(projects.get(0).getDescription()).isEqualTo("Napisać test");
//  }

//    @Test
//    void shouldFindProjectById() {
//
//        Project response = projectService.getProjectById(1L);
//
//        assertThat(response.getName()).isEqualTo("Zadanie 1");
//        assertThat(response.getDescription()).isEqualTo("Napisać test");
//    }
//
//    @Test
//    void shouldThrowNotFoundWhenProjectMissing() {
//
//        assertThatThrownBy(() -> projectService.getProjectById(999L)).isInstanceOf(
//                        ResponseStatusException.class)
//                .hasMessageContaining("404 NOT_FOUND")
//                .hasMessageContaining("Project not found");
//    }


}
