package com.library.libraryAutomation.service;

import com.library.libraryAutomation.dto.UserStats;
import com.library.libraryAutomation.entity.User;
import com.library.libraryAutomation.repository.BorrowRepository;
import com.library.libraryAutomation.repository.FineRepository;
import com.library.libraryAutomation.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final FineRepository fineRepository;

    public UserService(UserRepository userRepository, BorrowRepository borrowRepository, FineRepository fineRepository) {
        this.userRepository = userRepository;
        this.borrowRepository = borrowRepository;
        this.fineRepository = fineRepository;
    }

    // Вспомогательный метод для сборки статистики одного юзера
    public UserStats getUserStats(User user) {
        Long activeBooks = borrowRepository.countByUserIdAndReturnDateIsNull(user.getId());
        BigDecimal debt = fineRepository.getTotalDebtByUserId(user.getId());

        return new UserStats(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                activeBooks,
                debt
        );
    }

    public List<UserStats> getAllUsersStats() {
        return userRepository.findAll().stream()
                .map(this::getUserStats)
                .toList();
    }


    public UserStats getStatsById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getUserStats(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        Long activeBooks = borrowRepository.countByUserIdAndReturnDateIsNull(id);

        if (activeBooks > 0) {
            throw new RuntimeException("Can't delete! This user has not returned books.");
        }

        userRepository.deleteById(id);
    }


}