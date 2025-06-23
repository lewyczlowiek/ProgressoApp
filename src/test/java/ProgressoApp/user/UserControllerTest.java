package ProgressoApp.user;

import ProgressoApp.controllers.api.UserController;
import ProgressoApp.dto.request.UserRequestDTO;
import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private ObjectMapper objectMapper;

  private User sampleUser;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();

    sampleUser = new User();
    sampleUser.setUserId(1L);
    sampleUser.setFirstName("John");
    sampleUser.setLastName("Doe");
    sampleUser.setEmail("john.doe@example.com");
    sampleUser.setRole(Role.STUDENT);
    sampleUser.setNumberIndex("123456");

    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  void createUser_shouldReturnCreatedUser() throws Exception {
    UserRequestDTO dto = new UserRequestDTO(
        "John",                     // firstName
        "Doe",                      // lastName
        "123456",                   // numberIndex ✅
        "john.doe@example.com",     // email ✅
        "password",                 // password
        Role.STUDENT                // role
    );

    given(userService.createUser(any(UserRequestDTO.class))).willReturn(sampleUser);

    mockMvc.perform(post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.firstName").value("John"));
  }

  @Test
  void getUserById_shouldReturnUser() throws Exception {
    given(userService.findById(1L)).willReturn(sampleUser);

    mockMvc.perform(get("/api/user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("john.doe@example.com"));
  }

  @Test
  void getAllUsers_shouldReturnUserList() throws Exception {
    User secondUser = new User();
    secondUser.setUserId(2L);
    secondUser.setFirstName("Alice");
    secondUser.setLastName("Smith");
    secondUser.setEmail("alice.smith@example.com");
    secondUser.setRole(Role.STUDENT);
    secondUser.setNumberIndex("654321");

    given(userService.findAll()).willReturn(List.of(sampleUser, secondUser));

    mockMvc.perform(get("/api/user/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
        .andExpect(jsonPath("$[1].email").value("alice.smith@example.com"));
  }

  @Test
  void updateUser_shouldReturnUpdatedUser() throws Exception {
    UserRequestDTO dto = new UserRequestDTO(
        "Updated", "User", "updated@example.com", "000000", "newpass", Role.STUDENT);

    sampleUser.setFirstName("Updated");
    sampleUser.setLastName("User");
    sampleUser.setEmail("updated@example.com");

    given(userService.updateUser(eq(1L), any(UserRequestDTO.class))).willReturn(sampleUser);

    String json = """
        {
          "firstName": "Updated",
          "lastName": "User",
          "email": "updated@example.com",
          "numberIndex": "000000",
          "password": "newpass",
          "role": "STUDENT"
        }
        """;

    mockMvc.perform(put("/api/user/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Updated"))
        .andExpect(jsonPath("$.email").value("updated@example.com"));
  }

  @Test
  void deleteUser_shouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/user/1"))
        .andExpect(status().isNoContent());

    verify(userService).deleteUser(1L);
  }
}
