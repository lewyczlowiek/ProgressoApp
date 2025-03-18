package ProgressoApp.service;

import ProgressoApp.dto.RegisterRequest;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest registerRequest) {

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("The provided email already exists!");
        }

        // Check if student number already exists
        if (userRepository.existsByStudentNumber(registerRequest.getStudentNumber())) {
            throw new IllegalArgumentException("The provided student number already exists!");
        }

        // Create user from the registration request
        User user = User.builder()
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .studentNumber(registerRequest.getStudentNumber())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))  // Encode password
            .roles(Collections.singleton(Role.STUDENT))  // Assuming a default role
            .build();

        return userRepository.save(user);  // Save user to the repository
    }
}
