package ProgressoApp.repository;

import ProgressoApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNumberIndex(String numberIndex);
    boolean existsByEmail(String email);
    boolean existsByNumberIndex(String numberIndex);

}
