package ProgressoApp.repository;

import ProgressoApp.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

  Optional<Project> findByProjectId(Long projectId);

  <T> Page<Project> findAll(Specification<T> tSpecification, Pageable pageable);

  Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

  @Query("SELECT p FROM Project p JOIN p.users u WHERE u.email = :email")
  Page<Project> findByUserEmail(@Param("email") String email, Pageable pageable);

  @Query("SELECT p FROM Project p JOIN p.users u WHERE u.email = :email AND LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))")
  Page<Project> findByUserEmailAndNameContaining(@Param("email") String email,
      @Param("search") String search, Pageable pageable);


}

