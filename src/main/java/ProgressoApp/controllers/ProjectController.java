package ProgressoApp.controllers;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.User;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.ProjectService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/edit/{id}")
    public String showEditProjectForm(@PathVariable long id, Model model) {
        // Pobieramy projekt
        Project project = projectService.findById(id);
        if (project == null) {
            return "redirect:/index";  // Jeśli projekt nie istnieje, przekierowanie na stronę główną
        }

        // Formatowanie endDateTime na 'yyyy-MM-dd'T'HH:mm'
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedEndDateTime = project.getEndDateTime().format(formatter);

        // Dodanie projektu do modelu
        model.addAttribute("project", project);
        model.addAttribute("formattedEndDateTime", formattedEndDateTime);

        return "edit_project";  // Przekazanie modelu do widoku
    }



    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable long id, @ModelAttribute ProjectRequestDTO projectDto) {
        // Zaktualizowanie projektu na podstawie przekazanych danych
        projectService.updateProject(id, projectDto);
        return "redirect:/index"; // Po zapisaniu, przekierowanie na stronę z listą projektów
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

    @GetMapping("/add")
    public String showAddProjectForm(Model model) {
        ProjectRequestDTO emptyProject = new ProjectRequestDTO(
                "", // Empty project name
                "", // Empty description
                LocalDateTime.now(), // Default current time
                new ArrayList<>(), // Empty task list
                new HashSet<>(), // Empty user set
                LocalDateTime.now(),
                "active"
        );

        model.addAttribute("project", emptyProject);
        return "project_file"; // Return the view for the form
    }

    @PostMapping("/add")
    public String createProject(@ModelAttribute ProjectRequestDTO projectDto) {
        // Check if statusProject is null or empty and assign a default value
        if (projectDto.statusProject() == null || projectDto.statusProject().isEmpty()) {
            // Create a new ProjectRequestDTO with the default statusProject value
            projectDto = new ProjectRequestDTO(
                    projectDto.name(),
                    projectDto.description(),
                    projectDto.creationTimestamp(),
                    projectDto.tasks(),
                    projectDto.users(),
                    projectDto.endDateTime(),
                    "active" // Default value
            );
        }
        // Save the project
        projectService.createProject(projectDto);
        return "redirect:/index";  // Redirect after saving the project
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable long id) {
        projectService.deleteProject(id);  // Wywołanie metody usuwania
        return "redirect:/index";   // Przekierowanie po usunięciu projektu
    }


    @PostMapping("/add-person/{projectId}")
    public String addUsersToProject(@PathVariable Long projectId, @RequestParam(required = false) Set<Long> selectedUsers) {
        // Logowanie dla diagnostyki
        System.out.println("Próba dodania użytkowników do projektu o ID: " + projectId);
        System.out.println("Wybrani użytkownicy (ID): " + selectedUsers);

        // Pobranie projektu na podstawie ID
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        // Jeśli nie wybrano żadnych użytkowników, przekierowujemy na stronę /index
        if (selectedUsers == null || selectedUsers.isEmpty()) {
            return "redirect:/index";  // Przekierowanie na stronę główną, jeśli żaden użytkownik nie został wybrany
        }

        // Pobranie użytkowników, którzy są aktualnie przypisani do projektu
        Set<User> currentUsers = project.getUsers();

        // Pobranie użytkowników na podstawie ich ID
        Set<User> usersToAdd = new HashSet<>(userRepository.findAllById(selectedUsers));

        // Użytkownicy do usunięcia (odznaczeni w formularzu)
        Set<User> usersToRemove = new HashSet<>(currentUsers);
        usersToRemove.removeAll(usersToAdd); // Usuwamy tych, którzy są teraz zaznaczeni

        // Użytkownicy do dodania (zaznaczeni w formularzu, ale nie w projekcie)
        usersToAdd.removeAll(currentUsers); // Usuwamy tych, którzy są już przypisani

        // Przypisanie nowych użytkowników do projektu
        currentUsers.addAll(usersToAdd);
        currentUsers.removeAll(usersToRemove); // Usuwamy użytkowników, którzy zostali odznaczeni

        // Zapisanie zmian
        projectRepository.save(project);

        // Po zapisaniu zmian przekierowujemy na stronę z projektem lub listą projektów
        return "redirect:/index";  // Możesz dostosować tę ścieżkę, aby przekierować użytkownika na odpowiednią stronę
    }

    @GetMapping("/add-person/{projectId}")
    public String showAddPeopleToProjectForm(@PathVariable Long projectId, Model model) {
        // Pobieramy projekt na podstawie ID
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        // Pobieramy wszystkich użytkowników, których można przypisać do projektu
        List<User> users = userRepository.findAll();

        // Dodajemy projekt i listę użytkowników do modelu
        model.addAttribute("project", project);
        model.addAttribute("users", users);

        return "add_people_project";  // Widok do przypisywania użytkowników do projektu
    }

    @GetMapping("/details/{projectId}")
    public String showProjectDetails(@PathVariable Long projectId, Model model) {
        // Pobieramy projekt na podstawie ID
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        // Pobieramy wszystkich użytkowników, którzy są przypisani do projektu
        List<User> users = new ArrayList<>(project.getUsers());

        // Dodajemy projekt i listę użytkowników do modelu
        model.addAttribute("project", project);
        model.addAttribute("users", users);

        return "details_project";  // Widok do wyświetlania szczegółów projektu
    }

}