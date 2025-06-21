package ProgressoApp.repository;

import ProgressoApp.model.Task;
import ProgressoApp.model.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {


    List<TaskSubmission> findByTask(Task task);
}