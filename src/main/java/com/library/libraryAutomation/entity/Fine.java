package com.library.libraryAutomation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fines")
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "borrow_id")
    private Borrow borrow; // Связь с выдачей

    private BigDecimal amount; // Сумма штрафа

    private Boolean isPaid = false; // Статус оплаты

    // --- GETTER VE SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Borrow getBorrow() { return borrow; }
    public void setBorrow(Borrow borrow) { this.borrow = borrow; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean paid) { isPaid = paid; }
}