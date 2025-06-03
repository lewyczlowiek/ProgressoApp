package ProgressoApp.controllers;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.model.Project;
import ProgressoApp.service.ProjectService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/project")
public class ProjectController {

  private final ProjectService projectService;

  @Autowired
  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping("/all")
  public ResponseEntity<Page<ProjectResponseDTO>> getAllProjects(
      @RequestParam(name = "id", required = false) Long id,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "description", required = false) String description, Pageable pageable) {

    Map<String, Object> params = new HashMap<>();
    Optional.ofNullable(id).ifPresent(value -> params.put("id", value));
    Optional.ofNullable(name).ifPresent(value -> params.put("name", value));
    Optional.ofNullable(description).ifPresent(value -> params.put("description", value));

    if (params.isEmpty()) {
      return ResponseEntity.ok(projectService.getAllProjects(pageable));
    } else {
      return ResponseEntity.ok(projectService.getAllProjectsWithParams(params, pageable));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable long id,
      @RequestBody ProjectRequestDTO projectDto) {
    return ResponseEntity.ok(projectService.updateProject(id, projectDto).toProjectResponseDTO());
  }

  @PostMapping
  public ResponseEntity<ProjectResponseDTO> createProject(
      @RequestBody ProjectRequestDTO projectDto) {
    return ResponseEntity.ok(projectService.createProject(projectDto).toProjectResponseDTO());
  }

  @DeleteMapping
  public ResponseEntity<ProjectResponseDTO> deleteProject(@RequestParam long id) {
    return ResponseEntity.ok(projectService.deleteProject(id).toProjectResponseDTO());
  }

}
