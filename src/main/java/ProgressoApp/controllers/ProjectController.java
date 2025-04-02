package ProgressoApp.controllers;

import ProgressoApp.model.Project;
import ProgressoApp.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  @PostMapping
  public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
    projectService.createProject(project);
    return new ResponseEntity<>(project, HttpStatus.CREATED);
  }

  @PutMapping("/{projectId}")
  public ResponseEntity<Project> updateProject(@PathVariable Long projectId,
      @Valid @RequestBody Project project) {
    project.setProjectId(projectId); // Ensure the projectId is set correctly before updating
    projectService.updateProject(project);
    return new ResponseEntity<>(project, HttpStatus.OK);
  }

  @DeleteMapping("/{projectId}")
  public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
    projectService.deleteProject(projectId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<Project> getProject(@PathVariable Long projectId) {
    // Optional: You can add logic to check if the project exists before returning
    return new ResponseEntity<>(projectService.getProjectById(projectId), HttpStatus.OK);
  }
}
