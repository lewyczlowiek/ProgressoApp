package ProgressoApp.repository;

import ProgressoApp.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

  Optional<Project> findByProjectId(Long projectId);

  <T> Page<Project> findAll(Specification<T> tSpecification, Pageable pageable);
}

