package com.library.libraryAutomation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")  // Hibernate komandlari , db'den bilgi aliyor
public class Author {
    @Id //PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT
    private Long id;

    private String name;
    private String bio;

    @ManyToMany(mappedBy = "authors") //Book classinda gosterilmis , book classinda many to many gosterir
    @JsonIgnore //Okumak icin Json ceviriyor ve cevirdiyinde sonsuz book dongesine giriyor , onun karsisini almak icin JsonIgnor kullanilir
    private Set<Book> books = new HashSet<>();

    // --- GETTER VE SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Set<Book> getBooks() { return books; }
    public void setBooks(Set<Book> books) { this.books = books; }
}