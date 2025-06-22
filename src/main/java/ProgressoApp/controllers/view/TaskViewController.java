package ProgressoApp.controllers.view;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
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

    @Autowired
    public TaskViewController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public String showTaskDetails(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        return "task_details";
    }

    @GetMapping("/add")
    public String showCreateForm() {
        return "task_form";
    }

    @PostMapping
    public String createTask(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Integer taskOrder,
            @RequestParam TaskStatus taskStatus,
            @RequestParam Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        TaskRequestDTO dto = new TaskRequestDTO(name, description, taskOrder, taskStatus, projectId, dueDate);
        taskService.createTask(dto);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        return "task_edit";
    }

    @GetMapping("/tasks")
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

    @GetMapping("/tasks/addTasks/{projectId}")
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

    @PostMapping("/{id}")
    public String updateTask(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Integer taskOrder,
            @RequestParam TaskStatus taskStatus,
            @RequestParam Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        TaskRequestDTO dto = new TaskRequestDTO(name, description, taskOrder, taskStatus, projectId, dueDate);
        taskService.updateTask(id, dto);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
