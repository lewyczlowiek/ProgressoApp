package ProgressoApp.mapper;

import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toDto(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getNumberIndex(),
                null // lub user.getSubmissions().stream().map(...).toList() jeśli potrzebujesz
        );
    }
}
