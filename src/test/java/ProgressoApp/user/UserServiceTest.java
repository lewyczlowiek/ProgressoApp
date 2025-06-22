package ProgressoApp.user;

import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.dto.request.UserRequestDTO;
import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;

import ProgressoApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private RegisterDTO registerDTO;
  private UserRequestDTO userRequestDTO;

  @BeforeEach
  void setUp() {
    registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Jan");
    registerDTO.setLastName("Kowalski");
    registerDTO.setEmail("jan.kowalski@example.com");
    registerDTO.setPassword("password123");
    registerDTO.setNumberIndex("12345");

    userRequestDTO = new UserRequestDTO(
        "Anna",
        "Nowak",
        "anna.nowak@example.com",
        "pass456",
        "54321",
        Role.STUDENT
    );
  }

  @Test
  void saveUser_shouldThrowIfEmailExists() {
    when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> userService.saveUser(registerDTO))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Ten adres email jest już zajęty");

    verify(userRepository, never()).save(any());
  }

  @Test
  void saveUser_shouldThrowIfNumberIndexExists() {
    when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
    when(userRepository.existsByNumberIndex(registerDTO.getNumberIndex())).thenReturn(true);

    assertThatThrownBy(() -> userService.saveUser(registerDTO))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Numer indeksu jest już zajęty");

    verify(userRepository, never()).save(any());
  }

  @Test
  void saveUser_shouldEncodePasswordAndSave() {
    when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
    when(userRepository.existsByNumberIndex(registerDTO.getNumberIndex())).thenReturn(false);
    when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPass");

    userService.saveUser(registerDTO);

    verify(userRepository).save(argThat(user ->
        user.getEmail().equals(registerDTO.getEmail()) &&
            user.getPassword().equals("encodedPass") &&
            user.getRole() == Role.STUDENT));
  }

  @Test
  void createUser_shouldThrowIfEmailExists() {
    when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(true);

    assertThatThrownBy(() -> userService.createUser(userRequestDTO))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Użytkownik z takim e-mailem już istnieje!");

    verify(userRepository, never()).save(any());
  }

  @Test
  void createUser_shouldThrowIfNumberIndexExists() {
    when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(false);
    when(userRepository.existsByNumberIndex(userRequestDTO.numberIndex())).thenReturn(true);

    assertThatThrownBy(() -> userService.createUser(userRequestDTO))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Numer indesku już jest zajęty przez innego użytkownika!");

    verify(userRepository, never()).save(any());
  }

  @Test
  void createUser_shouldSaveUser() {
    when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(false);
    when(userRepository.existsByNumberIndex(userRequestDTO.numberIndex())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User saved = userService.createUser(userRequestDTO);

    assertThat(saved.getEmail()).isEqualTo(userRequestDTO.email());
    assertThat(saved.getRole()).isEqualTo(Role.STUDENT);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void updateUser_shouldUpdateFieldsAndEncodePassword() {
    User existing = User.builder()
        .userId(1L)
        .firstName("Old")
        .lastName("Name")
        .email("old@example.com")
        .password("oldpass")
        .numberIndex("11111")
        .role(Role.STUDENT)
        .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPass");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UserRequestDTO updateDTO = new UserRequestDTO("NewFirst", "NewLast", "22222",
        "new@example.com", "newPass", Role.STUDENT);

    User updated = userService.updateUser(1L, updateDTO);

    assertThat(updated.getFirstName()).isEqualTo("NewFirst");
    assertThat(updated.getLastName()).isEqualTo("NewLast");
    assertThat(updated.getEmail()).isEqualTo("new@example.com");
    assertThat(updated.getNumberIndex()).isEqualTo("22222");
    assertThat(updated.getPassword()).isEqualTo("encodedNewPass");
  }

  @Test
  void updateUser_shouldNotChangePasswordIfNullOrEmpty() {
    User existing = User.builder()
        .userId(1L)
        .password("oldpass")  // konieczne, by test zadziałał
        .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // ustawiamy pusty password w DTO
    UserRequestDTO updateDTO = new UserRequestDTO("f", "l", "index", "e@mail.com", "",
        Role.STUDENT);

    User updated = userService.updateUser(1L, updateDTO);

    assertThat(updated.getPassword()).isEqualTo("oldpass");  // hasło powinno pozostać bez zmian
  }

  @Test
  void deleteUser_shouldDeleteIfExists() {
    User user = new User();
    user.setUserId(1L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    userService.deleteUser(1L);

    verify(userRepository).delete(user);
  }

  @Test
  void findById_shouldThrowIfNotFound() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.findById(99L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Nie znaleziono użytkownika");
  }

  @Test
  void findById_shouldReturnUser() {
    User user = new User();
    user.setUserId(1L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    User result = userService.findById(1L);

    assertThat(result).isEqualTo(user);
  }

  @Test
  void findByEmail_shouldReturnOptionalUser() {
    User user = new User();
    user.setEmail("email@example.com");

    when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));

    Optional<User> result = userService.findByEmail("email@example.com");

    assertThat(result).isPresent();
  }

  @Test
  void findAll_shouldReturnList() {
    List<User> users = List.of(new User(), new User());

    when(userRepository.findAll()).thenReturn(users);

    List<User> result = userService.findAll();

    assertThat(result).hasSize(2);
  }

  @Test
  void getUsersPageFiltered_shouldReturnPageFilteredByEmail() {
    User user = User.builder()
        .email("test@example.com")
        .build();

    UserResponseDTO dto = user.toUserResponseDTO();

    Page<User> userPage = new PageImpl<>(List.of(user));
    Pageable pageable = PageRequest.of(0, 10);

    when(
        userRepository.findByEmailContainingIgnoreCase(eq("test@example.com"), any(Pageable.class)))
        .thenReturn(userPage);

    Page<UserResponseDTO> result = userService.getUsersPageFiltered("test@example.com", pageable,
        "email", "asc");

    assertThat(result).hasSize(1);
    assertThat(result.getContent().get(0).email()).isEqualTo("test@example.com");
  }

  @Test
  void getUsersPageFiltered_shouldReturnPageWithoutFilter() {
    User user = User.builder()
        .email("another@example.com")
        .build();

    Page<User> userPage = new PageImpl<>(List.of(user));
    Pageable pageable = PageRequest.of(0, 10);

    when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

    Page<UserResponseDTO> result = userService.getUsersPageFiltered(null, pageable, "email",
        "desc");

    assertThat(result).hasSize(1);
  }

  @Test
  void loadUserByUsername_shouldReturnUserDetails() {
    User user = new User();
    user.setEmail("email@test.com");

    when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.of(user));

    UserDetails userDetails = userService.loadUserByUsername("email@test.com");

    assertThat(userDetails.getUsername()).isEqualTo("email@test.com");
  }

  @Test
  void loadUserByUsername_shouldThrowIfNotFound() {
    when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.loadUserByUsername("unknown@test.com"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("Użytkownik nie istnieje");
  }
}
