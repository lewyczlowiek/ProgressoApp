package ProgressoApp.controllers;

import ProgressoApp.model.Project;
import ProgressoApp.service.ProjectService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectService projectService;

  @Autowired
  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping
  public List<Project> getAllProjects() {
    return projectService.getAllProjects();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
    return projectService.getProjectById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Project createProject(@RequestBody Project project) {
    return projectService.createProject(project);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Project> updateProject(@PathVariable Long id,
      @RequestBody Project project) {
    try {
      return ResponseEntity.ok(projectService.updateProject(id, project));
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")

  public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
    projectService.deleteProject(id);
    return ResponseEntity.noContent().build();
  }
}
