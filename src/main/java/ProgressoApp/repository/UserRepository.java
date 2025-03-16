package ProgressoApp.repository;

import ProgressoApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNumerIndeksu(String numerIndeksu);
    boolean existsByEmail(String email);
    boolean existsByNumerIndeksu(String numerIndeksu);

}
