package ProgressoApp.service;

import ProgressoApp.dto.RegisterDTO;
import ProgressoApp.model.User;
import java.util.Optional;
import java.util.List;
public interface UserService {

  void saveUser(RegisterDTO registerDTO);

  User findByEmail(String email);
  List<User> findAll(); // Dodaj metodę findAll()

}
