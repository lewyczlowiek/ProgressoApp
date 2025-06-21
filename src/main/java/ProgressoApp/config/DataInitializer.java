package ProgressoApp.config;

import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUser(
                "Admin", "Admin", "111111",
                "admin.admin@op.pl", "admin1234", Role.ADMIN);

        seedUser(
                "Użytkownik", "Wykładowca", "222222",
                "lecturer.lecturer@op.pl", "lecturer1234", Role.LECTURER);


        seedUser(
                "Użytkownik", "Student", "333333",
                "student.student@op.pl", "student1234", Role.STUDENT);

    }

    private void seedUser(String firstName, String lastName, String numberIndex, String email, String rawPassword, Role role) {

        if (userRepository.existsByEmail(email) || userRepository.existsByNumberIndex(numberIndex)) {
            return;
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .numberIndex(numberIndex)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        userRepository.save(user);
    }
}
