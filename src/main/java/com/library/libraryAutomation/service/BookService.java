package com.library.libraryAutomation.service;

import com.library.libraryAutomation.dto.BookRequest;
import com.library.libraryAutomation.entity.Author;
import com.library.libraryAutomation.entity.Book;
import com.library.libraryAutomation.repository.AuthorRepository;
import com.library.libraryAutomation.repository.BookRepository;
import com.library.libraryAutomation.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //Classin servis oldugunu belli ediyor
public class BookService {

    @Autowired
    private BookRepository bookRepository; //BookRepository kullaniryoruz bide new BookRepistory() yazmadan , Autowired yardimiyla

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public Book addBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setAvailable(true);

        if (request.getAuthorName() != null && !request.getAuthorName().isEmpty()) {
            String name = request.getAuthorName().trim();

            Author author = authorRepository.findByName(name)
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        newAuthor.setName(name);
                        newAuthor.setBio("Author bio placeholder");
                        return authorRepository.save(newAuthor);
                    });

            book.getAuthors().add(author);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.getAuthors().clear();
        bookRepository.save(book);

        borrowRepository.deleteFinesByBookId(id);
        borrowRepository.deleteByBook_Id(id);
        bookRepository.deleteById(id);
    }
}