package com.library.libraryAutomation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "fines")
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "borrow_id")
    private Borrow borrow; //Bir borcun bir cezasi olur

    private BigDecimal amount; //Parani saklamak icin cok kullanilir 0.1 + 0.2 = 0.3 gosterir ama float 0.30000004 gosterirdi

    private Boolean isPaid = false;
}