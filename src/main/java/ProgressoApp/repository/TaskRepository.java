package ProgressoApp.repository;

import ProgressoApp.model.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
       Page<Task> findAll(Specification<Task> spec, Pageable pageable);
       List<Task> findByProject_ProjectId(Long projectId);

       @Query("SELECT t FROM Task t JOIN t.users u WHERE u.userId = :userId")
       Page<Task> findTasksAssignedDirectly(@Param("userId") Long userId, Pageable pageable);

       @Modifying
       @Transactional
       @Query(value = "DELETE FROM task_user WHERE user_id = :userId", nativeQuery = true)
       void deleteAllTaskUserByUserId(@Param("userId") Long userId);

}
