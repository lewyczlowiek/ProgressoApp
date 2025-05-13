package ProgressoApp.dto.request;

import ProgressoApp.dto.response.TaskResponseDTO;
import ProgressoApp.dto.response.UserResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ProjectRequestDTO(String name, String description,
                                LocalDateTime creationTimestamp, List<TaskResponseDTO> tasks,
                                Set<UserResponseDTO> users) {

}
