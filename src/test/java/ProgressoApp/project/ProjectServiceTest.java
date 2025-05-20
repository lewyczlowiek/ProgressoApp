package ProgressoApp.project;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Test
    void shouldSaveAndFindProject() {
        ProjectRequestDTO dto = new ProjectRequestDTO(
                "Zadanie 1",
                "Napisać test",
                new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                new ArrayList<>(),
                new HashSet<>()
        );

        projectService.createProject(dto);
        List<Project> list = projectService.getAllProjects();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDescription()).isEqualTo("Napisać test");
    }
}
