package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.dto.response.TaskResponseDTO;
import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public TaskViewController(TaskService taskService, ProjectService projectService, UserService userService) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.userService = userService;
    }

    // ✅ Zakomentowana metoda wywołująca konflikt z MainController
    /*
    @GetMapping
    public String showTaskList(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks(Pageable.unpaged()).getContent());
        return "task_get";
    }
    */

    /*@GetMapping("/{id}")
    public String showTaskDetails(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        return "task_details";
    }
*/
    @GetMapping("/add")
    public String showCreateForm(Model model) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<ProjectResponseDTO> projects = projectService.getAllProjects(pageable).getContent();
        model.addAttribute("projects", projects);
        return "task_form";
    }

    @PostMapping
    public String createTask(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsers
    ) {
        TaskRequestDTO dto = new TaskRequestDTO(
                name,
                description,
                0, // lub taskOrder z formularza
                TaskStatus.TO_DO, // lub status z formularza
                projectId,
                dueDate
        );
        Long taskId = taskService.createTask(dto).getTaskId();

        if (selectedUsers != null) {
            taskService.assignUsersToTask(taskId, selectedUsers);
        }

        return "redirect:/tasks";
    }

    // Edycja — tylko formularz
/*
@GetMapping("/{id}/edit")
public String showEditForm(@PathVariable Long id, Model model) {
    Task task = taskService.findById(id);
    model.addAttribute("task", task);
    return "task_edit";
}
*/

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

    /*@GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        return "task_edit";
    }*/

    @GetMapping("/task/form-data")
    public ResponseEntity<?> getTaskFormData(@RequestParam(required = false) Long projectId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<ProjectResponseDTO> projects = projectService.getAllProjects(pageable).getContent();
        List<UserResponseDTO> users = projectId != null
                ? userService.getUsersByProjectId(projectId) // <-- WAŻNE
                : List.of();

        Map<String, Object> formData = new HashMap<>();
        formData.put("projects", projects);
        formData.put("users", users);

        return ResponseEntity.ok(formData);
    }

    @GetMapping("/tasks/form")
    public String showTaskForm(Model model) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<ProjectResponseDTO> projects = projectService.getAllProjects(pageable).getContent();
        model.addAttribute("projects", projects);
        return "task_form";
    }

}
