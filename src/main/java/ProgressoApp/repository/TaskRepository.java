package ProgressoApp.repository;

import ProgressoApp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
       Page<Task> findAll(Specification<Task> spec, Pageable pageable);
       List<Task> findByProject_ProjectId(Long projectId);
}