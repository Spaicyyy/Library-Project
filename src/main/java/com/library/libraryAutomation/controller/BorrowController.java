package com.library.libraryAutomation.controller;

import com.library.libraryAutomation.entity.Borrow;
import com.library.libraryAutomation.service.BorrowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // Kitap al: /api/borrow?userId=1&bookId=2
    @PostMapping
    public Borrow borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        return borrowService.borrowBook(userId, bookId);
    }

    // Kitap geri ver: /api/borrow/return/5
    @PostMapping("/return/{borrowId}")
    public Borrow returnBook(@PathVariable Long borrowId) {
        return borrowService.returnBook(borrowId);
    }

    // Benim kitaplarim: /api/borrow/my/1
    @GetMapping("/my/{userId}")
    public List<Borrow> getMyBorrows(@PathVariable Long userId) {
        return borrowService.getMyBorrows(userId);
    }
}