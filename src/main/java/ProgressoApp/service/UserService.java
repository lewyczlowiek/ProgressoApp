package ProgressoApp.service;

import ProgressoApp.dto.RegisterRequest;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Podany e-mail już istnieje!!!");
        }

        if (userRepository.existsByNumberIndex(registerRequest.getNumerIndex())) {
            throw new IllegalArgumentException("Podany numer indeksu już istnieje!!!");
        }

        User user  = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .roles(Collections.singleton(Role.STUDENT))
                .build();

        return userRepository.save(user);
    }
}
