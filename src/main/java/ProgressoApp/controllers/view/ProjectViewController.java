package ProgressoApp.controllers.view;

import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
}
