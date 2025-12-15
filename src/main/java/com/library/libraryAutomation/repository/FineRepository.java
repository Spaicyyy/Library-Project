package com.library.libraryAutomation.repository;

import com.library.libraryAutomation.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface FineRepository extends JpaRepository<Fine, Long> {

    //Kullanicini tum odenmemis cezalarini sayiyor
    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.borrow.user.id = :userId AND f.isPaid = false")
    BigDecimal getTotalDebtByUserId(@Param("userId") Long userId);
}