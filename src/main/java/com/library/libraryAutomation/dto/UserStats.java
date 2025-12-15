package com.library.libraryAutomation.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserStats {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private Long activeBooks; // Kac tane kitap elinde var
    private BigDecimal totalDebt; // Ne kadar cezasi var

    //constructor
    public UserStats(Long id, String fullName, String email, String role, Long activeBooks, BigDecimal totalDebt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.activeBooks = activeBooks;
        this.totalDebt = totalDebt == null ? BigDecimal.ZERO : totalDebt;
    }
}