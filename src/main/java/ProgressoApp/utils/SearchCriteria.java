package ProgressoApp.utils;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class SearchCriteria {

  private String key;
  private String operation; // Opcjonalne, np. "=", ">", "<", "like"
  private Object value;

  public SearchCriteria(String key, Object value) {
    this.key = key;
    this.value = value;
    this.operation = "="; // domyślnie
  }

  public SearchCriteria(String key, String operation, Object value) {
    this.key = key;
    this.operation = operation;
    this.value = value;
  }


  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }
}