package ProgressoApp.user;

import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

    @DataJpaTest
    @EntityScan(basePackages = "ProgressoApp.model")
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
    @ActiveProfiles("test")
    public class TaskRepositoryTest {

        @Autowired
        private TaskRepository taskRepository;

        @BeforeEach
        void setUp() {
            Task task = new Task("dwa", "opis", 3, null, TaskStatus.TO_DO);
            taskRepository.save(task);
        }

        @Test
        void shouldSaveAndFindTask() {
            List<Task> allTasks = taskRepository.findAll();
            assertThat(allTasks).hasSize(1);
            assertThat(allTasks.get(0).getDescription()).isEqualTo("opis");
        }
    }

