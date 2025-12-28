package com.library.libraryAutomation.controller;

import com.library.libraryAutomation.dto.UserStats;
import com.library.libraryAutomation.entity.User;
import com.library.libraryAutomation.repository.BorrowRepository;
import com.library.libraryAutomation.repository.FineRepository;
import com.library.libraryAutomation.repository.UserRepository;
import com.library.libraryAutomation.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserStats> getAllUsers() {
        return userService.getAllUsersStats();
    }

    // Ogrenci sadece kendi borcunu goruyor
    @GetMapping("/me/{id}")
    public UserStats getMyStats(@PathVariable Long id) {
        return userService.getStatsById(id);
    }

    // 2.Yeni kullanici ekle
    @PostMapping
    public User addUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken!");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    // 3. Kullanicini sil (Once cezasina bakiyoruz)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        // Просто вызываем сервис
        userService.deleteUser(id);
    }
}