package ProgressoApp.dto.response;

import ProgressoApp.model.Role;
import ProgressoApp.model.TaskSubmission;
import java.util.List;

public record UserResponseDTO(Long userId, String firstName, String lastName, String email,
                              Role role, String numberIndex,
                              List<TaskSubmissionResponseDTO> submissions) {

}
