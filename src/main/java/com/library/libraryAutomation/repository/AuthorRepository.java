package com.library.libraryAutomation.repository;

import com.library.libraryAutomation.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name); //Optional secim veriyor , author varsa yazdirir , yoksa .empty() verir ama null vermes!
}