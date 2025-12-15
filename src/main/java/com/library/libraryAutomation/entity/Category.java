package com.library.libraryAutomation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data //Lombok kendisi Setter ve Getterler yapiyor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}