package ProgressoApp.controllers.view;

import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.UserService;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectViewController {

  public final ProjectService projectService;
  public final UserService userService;
  public final TaskService taskService;

  @GetMapping("/details/{projectId}")
  public String showProjectDetails(@PathVariable Long projectId, Model model) {
    Project project = projectService.findById(projectId);

    List<User> users = new ArrayList<>(project.getUsers());

    List<Task> allTasks = project.getTasks();

    List<Task> todoTasks = allTasks.stream()
        .filter(task -> TaskStatus.TO_DO.equals(task.getTaskStatus()))
        .collect(Collectors.toList());

    List<Task> inProgressTasks = allTasks.stream()
        .filter(task -> TaskStatus.IN_PROGRESS.equals(task.getTaskStatus()))
        .collect(Collectors.toList());

    List<Task> doneTasks = allTasks.stream()
        .filter(task -> TaskStatus.DONE.equals(task.getTaskStatus()))
        .collect(Collectors.toList());

    model.addAttribute("project", project);
    model.addAttribute("users", users);
    model.addAttribute("todoTasks", todoTasks);
    model.addAttribute("inProgressTasks", inProgressTasks);
    model.addAttribute("doneTasks", doneTasks);

    return "details_project";
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @GetMapping("/add-person/{projectId}")
  public String showAddPeopleToProjectForm(@PathVariable Long projectId, Model model) {
    Project project = projectService.findById(projectId);
    List<User> users = userService.findAll();

    model.addAttribute("project", project);
    model.addAttribute("users", users);

    return "add_people_project";
  }

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
}
