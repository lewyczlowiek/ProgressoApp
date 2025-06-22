package ProgressoApp.controllers.view;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.request.TaskSubmissionRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.TaskSubmissionService;
import ProgressoApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

  private final TaskService taskService;
  private final ProjectService projectService;
  private final UserService userService;
  private final TaskSubmissionService taskSubmissionService;

  @Autowired
  public TaskViewController(TaskService taskService, ProjectService projectService,
      UserService userService, TaskSubmissionService taskSubmissionService) {
    this.taskService = taskService;
    this.projectService = projectService;
    this.userService = userService;
    this.taskSubmissionService = taskSubmissionService;
  }

  @GetMapping("/details/{id}")
  public String showTaskDetails(@PathVariable Long id, Model model) {
    Task task = taskService.findById(id);
    model.addAttribute("task", task);
    return "task_details";
  }

  @GetMapping("/add")
  public String showCreateForm() {
    return "task_form";
  }


  @GetMapping("/{id}/edit")
  public String editTaskForm(@PathVariable Long id, Model model) {
    Task task = taskService.findById(id);
    model.addAttribute("task", task);
    return "task_edit";
  }

  @GetMapping()
  public String showTasks(Model model) {
    List<Task> tasks = taskService.findAll();

    // Formatowanie daty do Stringa
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Mapowanie zadań z dodaną sformatowaną datą
    List<Map<String, Object>> taskList = tasks.stream().map(task -> {
      Map<String, Object> taskMap = new HashMap<>();
      taskMap.put("taskId", task.getTaskId());
      taskMap.put("name", task.getName());
      taskMap.put("taskStatus", task.getTaskStatus());
      taskMap.put("formattedCreationDate", task.getCreationTimestamp().format(formatter));
      return taskMap;
    }).collect(Collectors.toList());

    model.addAttribute("tasks", taskList);
    return "task_get";
  }

  @GetMapping("/addTasks/{projectId}")
  public String showAddTaskForm(@PathVariable Long projectId, Model model) {
    TaskRequestDTO taskDTO = new TaskRequestDTO(
        "", "", 1, null, projectId, null // status null - ustalisz na backendzie
    );
    model.addAttribute("task", taskDTO);

    Project project = projectService.findById(projectId);

    Set<User> assignedUsersSet = project.getUsers();
    List<User> assignedUsers = new ArrayList<>(assignedUsersSet);

    model.addAttribute("assignedUsers", assignedUsers);
    model.addAttribute("project", project);

    return "task_file"; // nazwa szablonu formularza
  }


}
