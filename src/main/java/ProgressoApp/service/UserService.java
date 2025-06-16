package ProgressoApp.service;

import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.dto.request.UserRequestDTO;
import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserResponseDTO saveUser(RegisterDTO registerDTO) {
    if (userRepository.existsByEmail(registerDTO.getEmail())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
    }

    if (userRepository.existsByNumberIndex(registerDTO.getNumberIndex())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number index already in use");
    }

    User user = User.builder()
            .firstName(registerDTO.getFirstName())
            .lastName(registerDTO.getLastName())
            .email(registerDTO.getEmail())
            .password(passwordEncoder.encode(registerDTO.getPassword()))
            .numberIndex(registerDTO.getNumberIndex())
            .role(Role.STUDENT) // lub domyślna logika roli
            .build();

    User saved = userRepository.save(user);

    return saved.toUserResponseDTO();
  }

  public User createUser(UserRequestDTO dto){
    if (userRepository.existsByEmail(dto.email())){
      throw new ResponseStatusException(HttpStatus.CONFLICT,  "Użytkownik z takim e-mailem już istnieje!");
    }

    if (userRepository.existsByNumberIndex(dto.numberIndex())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Numer indesku już jest zajęty przez innego użytkownika!");
    }

    User user = User.builder()
            .firstName(dto.firstName())
            .lastName(dto.lastName())
            .email(dto.email())
            .password(dto.password())
            .numberIndex(dto.numberIndex())
            .role(Role.STUDENT)
            .build();

      return userRepository.save(user);
  }

  public User updateUser(Long id, UserRequestDTO dto) {
    User user = findById(id);

    user.setFirstName(dto.firstName());
    user.setLastName(dto.lastName());
    user.setEmail(dto.email());
    user.setNumberIndex(dto.numberIndex());

    if (dto.password() != null && !dto.password().isEmpty()) {
      user.setPassword(passwordEncoder.encode(dto.password()));
    }
    return userRepository.save(user);
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = findById(id);
    userRepository.delete(user);
  }

  public User findById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika"));
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie istnieje: " + email));
  }
}
