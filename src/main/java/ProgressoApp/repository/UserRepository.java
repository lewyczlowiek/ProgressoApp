package ProgressoApp.repository;

import ProgressoApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByEmail(String email);

  Optional<User> findByNumberIndex(String numberIndex);

  boolean existsByEmail(String email);

  boolean existsByNumberIndex(String numberIndex);

}
