package ProgressoApp.service;

import ProgressoApp.dto.RegisterDTO;
import ProgressoApp.model.User;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  void saveUser(RegisterDTO registerDTO);

  Optional<User> findByEmail(String email);
}
