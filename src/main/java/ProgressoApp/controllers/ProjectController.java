package ProgressoApp.controllers;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.model.Project;
import ProgressoApp.service.ProjectService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/*

@Controller
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

  @DeleteMapping
  public ResponseEntity<ProjectResponseDTO> deleteProject(@RequestParam long id) {
    return ResponseEntity.ok(projectService.deleteProject(id).toProjectResponseDTO());
  }

  @GetMapping("/add")
  public String showAddProjectForm(Model model) {
    ProjectRequestDTO emptyProject = new ProjectRequestDTO(
            "", // Empty project name
            "", // Empty description
            LocalDateTime.now(), // Default current time
            new ArrayList<>(), // Empty task list
            new HashSet<>() // Empty user set
    );

    model.addAttribute("project", emptyProject);
    return "project_file"; // Return the view for the form
  }

  @PostMapping("/add")
  public String createProject(@ModelAttribute ProjectRequestDTO projectDto) {
    projectService.createProject(projectDto);  // Zapisanie projektu
    return "redirect:/api/project/";  // Przekierowanie po zapisaniu
  }

  @GetMapping("/")
  public String showProjectsPage(Model model, Pageable pageable) {
    Page<ProjectResponseDTO> projects = projectService.getAllProjects(pageable);  // Pobierz projekty z serwisu
    model.addAttribute("projects", projects.getContent());  // Dodaj projekty do modelu
    return "index";  // Zwróć widok "index.html"
  }

}*/
@Controller
@RequestMapping("/api/project")
public class ProjectController {

  private final ProjectService projectService;

  @Autowired
  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

/*  @GetMapping("/{id}/edit")
  public String showEditProjectForm(@PathVariable long id, Model model) {
    // Pobieramy projekt, który ma zostać edytowany
    Project project = projectService.findById(id);
    model.addAttribute("project", project);
    return "edit_project"; // Strona edycji projektu
  }*/
@GetMapping("/edit/{id}")
public String showEditProjectForm(@PathVariable long id, Model model) {
  // Logowanie dla diagnostyki
  System.out.println("Próba edycji projektu o ID: " + id);

  // Pobieramy projekt, który ma zostać edytowany
  Project project = projectService.findById(id);
  if (project == null) {
    System.out.println("Projekt o ID " + id + " nie znaleziony.");
    return "redirect:/"; // Przekierowanie na stronę główną, jeśli projekt nie istnieje
  }

  model.addAttribute("project", project);
  return "edit_project"; // Zwróć widok formularza edycji
}


  @PostMapping("/{id}/edit")
  public String updateProject(@PathVariable long id, @ModelAttribute ProjectRequestDTO projectDto) {
    // Zaktualizowanie projektu na podstawie przekazanych danych
    projectService.updateProject(id, projectDto);
    return "redirect:/api/project/"; // Po zapisaniu, przekierowanie na stronę z listą projektów
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

 /* @DeleteMapping
  public ResponseEntity<ProjectResponseDTO> deleteProject(@RequestParam long id) {
    return ResponseEntity.ok(projectService.deleteProject(id).toProjectResponseDTO());
  }*/

  @GetMapping("/add")
  public String showAddProjectForm(Model model) {
    ProjectRequestDTO emptyProject = new ProjectRequestDTO(
            "", // Empty project name
            "", // Empty description
            LocalDateTime.now(), // Default current time
            new ArrayList<>(), // Empty task list
            new HashSet<>() // Empty user set
    );

    model.addAttribute("project", emptyProject);
    return "project_file"; // Return the view for the form
  }

  @PostMapping("/add")
  public String createProject(@ModelAttribute ProjectRequestDTO projectDto) {
    projectService.createProject(projectDto);  // Zapisanie projektu
    return "redirect:/api/project/";  // Przekierowanie po zapisaniu
  }

  @GetMapping("/")
  public String showProjectsPage(Model model, Pageable pageable) {
    Page<ProjectResponseDTO> projects = projectService.getAllProjects(pageable);  // Pobierz projekty z serwisu
    model.addAttribute("projects", projects.getContent());  // Dodaj projekty do modelu
    return "index";  // Zwróć widok "index.html"
  }

  @GetMapping("/delete/{id}")
  public String deleteProject(@PathVariable long id) {
    projectService.deleteProject(id);  // Wywołanie metody usuwania
    return "redirect:/api/project/";   // Przekierowanie po usunięciu projektu
  }
}

