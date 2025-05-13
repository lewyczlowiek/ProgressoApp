package ProgressoApp.dto.response;

import ProgressoApp.model.Task;
import ProgressoApp.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ProjectResponseDTO(Long projectId, String name, String description,
                                 @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
                                 LocalDateTime creationTimestamp, List<TaskResponseDTO> tasks,
                                 Set<UserResponseDTO> users) {

}
