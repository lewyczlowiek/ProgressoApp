package ProgressoApp.model;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.response.TaskResponseDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "task_id")
  private Long taskId;

  @NotBlank(message = "Task name cannot be empty!")
  @Column(nullable = false, length = 50)
  private String name;

  private Integer taskOrder;

  @Column(length = 1000)
  private String description;

  @Enumerated(EnumType.STRING)
  private TaskStatus taskStatus;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, name = "creation_timestamp")
  private LocalDateTime creationTimestamp;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TaskSubmission> submissions;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  public Task(Task task) {
    this.name = task.name;
    this.description = task.description;
    this.taskOrder = task.taskOrder;
    this.creationTimestamp = task.creationTimestamp;
    this.taskStatus = task.taskStatus;
    this.submissions = task.submissions;
  }

  public Task() {

  }

  public Task(TaskRequestDTO taskRequestDTO) {
    this.taskOrder = taskRequestDTO.taskOrder();
    this.name = taskRequestDTO.name();
    this.description = taskRequestDTO.description();
    this.taskStatus = taskRequestDTO.taskStatus();
    this.dueDate = taskRequestDTO.dueDate();

  }

  public TaskResponseDTO toTaskResponseDTO() {
    return new TaskResponseDTO(
        this.taskId,
        this.name,
        this.description,
        this.taskOrder != null ? this.taskOrder.toString() : null,
        this.taskStatus,
        this.project != null ? this.project.getProjectId() : null,
        this.creationTimestamp,
        this.dueDate,
        this.submissions != null ? this.submissions.stream()
            .map(TaskSubmission::toTaskSubmissionResponseDTO).toList() : List.of()
    );
  }


  public Integer getTaskOrder() {
    return taskOrder;
  }

  public void setTaskOrder(Integer order) {
    this.taskOrder = order;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(LocalDateTime creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public TaskStatus getTaskStatus() {
    return taskStatus;
  }

  public void setTaskStatus(TaskStatus taskStatus) {
    this.taskStatus = taskStatus;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }
}


