package ProgressoApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

  private TaskStatus taskStatus;
  @CreationTimestamp
  @Column(nullable = false, updatable = false, name = "creation_timestamp")
  private LocalDateTime creationTimestamp;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  public Task(String name, String description, Integer taskOrder, LocalDateTime creationTimestamp,
      TaskStatus taskStatus) {
    this.name = name;
    this.description = description;
    this.taskOrder = taskOrder;
    this.creationTimestamp = creationTimestamp;
    this.taskStatus = taskStatus;
  }

  public Task() {

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
}


