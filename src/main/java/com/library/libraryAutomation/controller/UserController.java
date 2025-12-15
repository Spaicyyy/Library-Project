package com.library.libraryAutomation.controller;

import com.library.libraryAutomation.dto.UserStats;
import com.library.libraryAutomation.entity.User;
import com.library.libraryAutomation.repository.BorrowRepository;
import com.library.libraryAutomation.repository.FineRepository;
import com.library.libraryAutomation.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final FineRepository fineRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, BorrowRepository borrowRepository, FineRepository fineRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.borrowRepository = borrowRepository;
        this.fineRepository = fineRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Herkesi stastik olarak alma
    @GetMapping
    public List<UserStats> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserStats> statsList = new ArrayList<>();

        for (User user : users) {
            // Kitap sayisi
            Long activeBooks = borrowRepository.countActiveBorrows(user.getId());
            // Ceza sayisi
            BigDecimal debt = fineRepository.getTotalDebtByUserId(user.getId());

            statsList.add(new UserStats(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole().name(),
                    activeBooks,
                    debt
            ));
        }
        return statsList;
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
        Long activeBooks = borrowRepository.countActiveBorrows(id);
        if (activeBooks > 0) {
            throw new RuntimeException("Can't delete! This user have not returned books.");
        }
        userRepository.deleteById(id);
    }
}