package com.library.libraryAutomation.repository;

import com.library.libraryAutomation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); //Kullanicini email'e gore bul
}