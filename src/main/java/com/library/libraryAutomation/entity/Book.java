package com.library.libraryAutomation.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String isbn;
    private boolean isAvailable;

    @ManyToMany(fetch = FetchType.EAGER) //Eager author'lari yada book'lari cikardigimizda digerinde hizli automatik cikmasini saglar
    @JoinTable(
            name = "book_authors", //db'de ismi
            joinColumns = @JoinColumn(name = "book_id"), //classin id'si
            inverseJoinColumns = @JoinColumn(name = "author_id") //diger classin id'si
    )
    private Set<Author> authors = new HashSet<>();

    // --- GETTER VE SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Set<Author> getAuthors() { return authors; }
    public void setAuthors(Set<Author> authors) { this.authors = authors; }
}