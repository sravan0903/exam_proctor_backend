package com.exam.proctor.config;


import com.exam.proctor.entity.Role;
import com.exam.proctor.entity.User;
import com.exam.proctor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ✅ Check if admin already exists
        if (userRepository.findByEmail("admin@exam.com").isEmpty()) {

            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@exam.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);

//            System.out.println("✅ Default ADMIN created");
//            System.out.println("📧 Email: admin@exam.com");
//            System.out.println("🔑 Password: admin123");
        }
    }
}

