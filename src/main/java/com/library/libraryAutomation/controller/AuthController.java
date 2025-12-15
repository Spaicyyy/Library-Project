package com.library.libraryAutomation.controller;

import com.library.libraryAutomation.entity.User;
import com.library.libraryAutomation.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController //Html bilgisini gondermekdense Json bilgilerini gonderiyor
@RequestMapping("/api/auth") // Controllerin yolu
@CrossOrigin(origins = "*") // her frondend icin yol aciyor
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> loginData) { //RequestMap Json bilgini alib Java'ya ceviriyor , burda ise Map'a ceviriyor
        String email = loginData.get("email");
        String password = loginData.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User don't found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Wrong password!");
        }
        return user;
    }
}