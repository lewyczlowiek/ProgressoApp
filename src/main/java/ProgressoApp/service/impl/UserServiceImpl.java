package ProgressoApp.service.impl;

import ProgressoApp.dto.RegisterDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void saveUser(RegisterDTO registerDTO) {
    User user = User.builder()
        .firstName(registerDTO.getFirstName())
        .lastName(registerDTO.getLastName())
        .email(registerDTO.getEmail())
        .password(registerDTO.getPassword())
        .numberIndex(registerDTO.getNumberIndex())
        .roles(Collections.singleton(Role.STUDENT))
        .build();

    userRepository.save(user);
  }

  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
}
