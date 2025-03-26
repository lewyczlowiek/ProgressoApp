package ProgressoApp.model;

public enum Role {
  STUDENT,
  LECTURER,
  ADMIN;

  public String getRole() {
    return "ROLE_" + this.name();
  }
}
