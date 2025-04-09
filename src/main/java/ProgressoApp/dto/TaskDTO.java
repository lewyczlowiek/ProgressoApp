package ProgressoApp.dto;

import ProgressoApp.model.TaskStatus;

public class TaskDTO {

  private String name;
  private String description;
  private Integer taskOrder;
  private TaskStatus taskStatus;
  private Long projectId;

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

  public Integer getTaskOrder() {
    return taskOrder;
  }

  public void setTaskOrder(Integer taskOrder) {
    this.taskOrder = taskOrder;
  }

  public TaskStatus getTaskStatus() {
    return taskStatus;
  }

  public void setTaskStatus(TaskStatus taskStatus) {
    this.taskStatus = taskStatus;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }
}
