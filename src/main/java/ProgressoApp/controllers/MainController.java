/*
package ProgressoApp.controllers;

import ProgressoApp.dto.response.ProjectResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

  @GetMapping("/")
  public String homePage() {
    return "login";
  }

*/
/*  @GetMapping("/index")
  public String loginPage() {
    return "index";
  }*//*


  @GetMapping("/index")  // Zachowujemy "index" jako domyślną stronę
  public String showProjectsPage(
          @RequestParam(value = "search", required = false) String search,
          @RequestParam(value = "sort", required = false, defaultValue = "creationTimestamp") String sort,
          @RequestParam(value = "dir", required = false, defaultValue = "desc") String dir,
          Model model, Pageable pageable) {

    Page<ProjectResponseDTO> projects;

    // Pobierz projekty na podstawie parametrów
    if (search != null && !search.isBlank()) {
      projects = projectService.getProjectsByNameContaining(search, pageable, sort, dir);
    } else {
      projects = projectService.getAllProjects(pageable, sort, dir);
    }

    model.addAttribute("projects", projects.getContent());

    // Parametry do formularza
    Map<String, String> params = new HashMap<>();
    params.put("search", search != null ? search : "");
    params.put("sort", sort);
    params.put("dir", dir);
    model.addAttribute("param", params);

    return "index";  // Zwracamy widok 'index'
  }
}
*/
package ProgressoApp.controllers;

import ProgressoApp.service.ProjectService;
import ProgressoApp.dto.response.ProjectResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

  private final ProjectService projectService;

  // Konstruktor z wstrzykiwaniem zależności
  @Autowired
  public MainController(ProjectService projectService) {
    this.projectService = projectService;
  }
  @GetMapping("/")
  public String homePage() {
    return "login";
  }
  @GetMapping("/index")
  public String showProjectsPage(
          @RequestParam(value = "search", required = false) String search,
          @RequestParam(value = "sort", required = false, defaultValue = "creationTimestamp") String sort,
          @RequestParam(value = "dir", required = false, defaultValue = "desc") String dir,
          Model model, Pageable pageable) {

    Page<ProjectResponseDTO> projects;

    // Pobierz projekty na podstawie parametrów
    if (search != null && !search.isBlank()) {
      projects = projectService.getProjectsByNameContaining(search, pageable, sort, dir);
    } else {
      projects = projectService.getAllProjects(pageable, sort, dir);
    }

    model.addAttribute("projects", projects.getContent());

    // Parametry do formularza
    Map<String, String> params = new HashMap<>();
    params.put("search", search != null ? search : "");
    params.put("sort", sort);
    params.put("dir", dir);
    model.addAttribute("param", params);

    return "index";  // Zwracamy widok 'index'
  }
}
