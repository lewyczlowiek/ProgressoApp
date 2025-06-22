package ProgressoApp.repository;

import ProgressoApp.model.Project;
import ProgressoApp.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByNumberIndex(String numberIndex);

  boolean existsByEmail(String email);

  boolean existsByNumberIndex(String numberIndex);

  Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

  Optional<User> findByEmail(String email);


}
