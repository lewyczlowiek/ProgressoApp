package ProgressoApp.service.impl;

import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void saveUser(RegisterDTO registerDTO) {

    User user = User.builder()
        .firstName(registerDTO.getFirstName())
        .lastName(registerDTO.getLastName())
        .email(registerDTO.getEmail())
        .password(passwordEncoder.encode(registerDTO.getPassword()))
        .numberIndex(registerDTO.getNumberIndex())
        .role(Role.STUDENT)
        .build();

    userRepository.save(user);
  }

  @Override
  public Optional<User>
  findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }
}
