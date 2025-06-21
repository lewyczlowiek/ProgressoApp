package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;

    @Autowired
    public TaskViewController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ✅ Zakomentowana metoda wywołująca konflikt z MainController
    /*
    @GetMapping
    public String showTaskList(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks(Pageable.unpaged()).getContent());
        return "task_get";
    }
    */

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
}
