package ProgressoApp.service;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.response.TaskResponseDTO;
import ProgressoApp.model.Task;
import ProgressoApp.model.Project;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.utils.TaskSpecification;
import ProgressoApp.utils.SearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            ProjectService projectService
    ) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
    }


    public Task createTask(TaskRequestDTO dto) {
        Task task = new Task(dto);

        Project project = projectService.findById(dto.projectId());
        task.setProject(project);

        return taskRepository.save(task);
    }


    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(pageable);
        return page.map(Task::toTaskResponseDTO);
    }


    public Page<TaskResponseDTO> getAllTasksWithParams(Map<String, Object> filter, Pageable pageable) {
        List<Specification<Task>> specs = filter.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    SearchCriteria sc = new SearchCriteria(key, value);

                    if ("name".equalsIgnoreCase(key)) {
                        sc.setOperation("like");
                    }

                    return (Specification<Task>) new TaskSpecification(sc);
                })
                .collect(Collectors.toList());

        Specification<Task> finalSpec = specs.stream()
                .reduce(Specification.where(null), Specification::and);

        Page<Task> page = taskRepository.findAll(finalSpec, pageable);
        return page.map(Task::toTaskResponseDTO);
    }


    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }


    public Task updateTask(Long id, TaskRequestDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        task.setName(dto.name());
        task.setDescription(dto.description());
        task.setTaskOrder(dto.taskOrder());
        task.setTaskStatus(dto.taskStatus());
        task.setDueDate(dto.dueDate());

        if (dto.projectId() != null) {
            Project project = projectService.findById(dto.projectId());
            task.setProject(project);
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.delete(task);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
