package ProgressoApp.service;

import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.Task;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

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

  public User findById(long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public Optional<User>
  findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }


}
