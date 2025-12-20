package com.library.libraryAutomation;

import com.library.libraryAutomation.entity.User;
import com.library.libraryAutomation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component //Bunun la , spring main'ni activ edenden sonra automatik bunuda aktif ediyor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Admin var mi diye bakiyoruz , yoksa ekliyoruz
        if (userRepository.findByEmail("admin@library.com").isEmpty()) {
            User admin = new User();
            admin.setFullName("Admin");
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // yeni sifresi , heslenmis
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            System.out.println("🚀 Admin created: admin@library.com / admin123");
        }
    }
}