package com.library.libraryAutomation.service;

import com.library.libraryAutomation.entity.Book;
import com.library.libraryAutomation.entity.Borrow;
import com.library.libraryAutomation.entity.User;
import com.library.libraryAutomation.repository.BookRepository;
import com.library.libraryAutomation.repository.BorrowRepository;
import com.library.libraryAutomation.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public BorrowService(BorrowRepository borrowRepository, BookRepository bookRepository,
                         UserRepository userRepository, EmailService emailService) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Borrow borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book already taken!");
        }

        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDueDate(LocalDate.now().plusWeeks(2));

        book.setAvailable(false);
        bookRepository.save(book);

        Borrow savedBorrow = borrowRepository.save(borrow);

        try {
            emailService.sendEmail(user.getEmail(), "KTU Library: Book borrowed 📖",
                    "Hi, " + user.getFullName() + "!\nyou taked book: " + book.getTitle());
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
        return savedBorrow;
    }

    @Transactional
    public Borrow returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("record not found"));

        if (borrow.getReturnDate() != null) throw new RuntimeException("Already returned!");

        borrow.setReturnDate(LocalDate.now());
        Book book = borrow.getBook();

        book.setAvailable(true);
        bookRepository.save(book);

        try {
            emailService.sendEmail(borrow.getUser().getEmail(), "KTU Library: Book returned ✅",
                    "Thanks! Book accepted.");
        } catch (Exception e) {
            System.out.println("Error email: " + e.getMessage());
        }

        return borrowRepository.save(borrow);
    }

    public List<Borrow> getMyBorrows(Long userId) {
        return borrowRepository.findByUserId(userId);
    }

}