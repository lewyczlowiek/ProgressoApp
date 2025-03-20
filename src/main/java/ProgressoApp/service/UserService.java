package ProgressoApp.service;

import ProgressoApp.dto.RegisterDTO;
import ProgressoApp.model.User;
import java.util.Optional;

public interface UserService {

  void saveUser(RegisterDTO registerDTO);

  User findByEmail(String email);
}
