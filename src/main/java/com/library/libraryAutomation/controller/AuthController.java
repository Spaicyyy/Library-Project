package com.library.libraryAutomation.controller;

import com.library.libraryAutomation.entity.User;
import com.library.libraryAutomation.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RestController //Html bilgisini gondermekdense Json bilgilerini gonderiyor
@RequestMapping("/api/auth") // Controllerin yolu
@CrossOrigin(origins = "*") // her frondend icin yol aciyor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password"); // Это "12345"

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong password!");
        }
        return user;
    }
}