package com.library.libraryAutomation.repository;

import com.library.libraryAutomation.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByUserId(Long userId); //Bir ogrencinin tum kitaplarini gostermek

    List<Borrow> findByReturnDateIsNull(); //Kitaplari hala vermiyenleri gosterir

    // 1. Kitapin cezalarini sil
    @Modifying //Bu funksiyona SELECT olmadigini gosteriyor
    @Transactional //Silmeyin guvenli gecmesine garanti veriyor
    @Query(value = "DELETE FROM fines WHERE borrow_id IN (SELECT id FROM borrows WHERE book_id = :bookId)", nativeQuery = true) //SQL yazilmis kod
    void deleteFinesByBookId(@Param("bookId") Long bookId); //Param , bookId kelimesini db'ile birlesdiriyor

    // 2. Kitapin verinme tarihcesini siliyor
    @Modifying
    @Transactional
    void deleteByBook_Id(Long bookId);

    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.user.id = :userId AND b.returnDate IS NULL") //JPQL yazilmis , yani Borrow b , kullanilmis
    Long countActiveBorrows(@Param("userId") Long userId);
}