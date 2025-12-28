-- 1. СREATE DATABASE
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP DATABASE library_db;
CREATE DATABASE library_db;
USE library_db;

-- 2. AUTHOR TABLE
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    bio TEXT
);

-- 3. CATEGORIES TABLE
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 4. USER TABLE
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STUDENT') DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. BOOK TABLE
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    category_id BIGINT,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 6. BOOK AUTHOR TABLE
CREATE TABLE book_authors (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

-- 7. BORROWING TABLE
CREATE TABLE borrows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- 8. FINE TABLE
CREATE TABLE fines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrow_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (borrow_id) REFERENCES borrows(id)
);

-- 9.STORED PROCEDURE
-- "tetikleyici (TRIGGER) ve prosedür (STORED PROCEDURE)"
-- Borrowing book procedure(checking availability)
DELIMITER //
CREATE PROCEDURE sp_borrow_book(IN p_user_id BIGINT, IN p_book_id BIGINT, IN p_days INT)
BEGIN
    DECLARE v_available BOOLEAN;
    SELECT is_available INTO v_available FROM books WHERE id = p_book_id;
    IF v_available THEN
        INSERT INTO borrows (user_id, book_id, borrow_date, due_date)
        VALUES (p_user_id, p_book_id, CURDATE(), DATE_ADD(CURDATE(), INTERVAL p_days DAY));
        UPDATE books SET is_available = FALSE WHERE id = p_book_id;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The book has already been issued!';
    END IF;
END //
DELIMITER ;

-- 10.Trigger
-- Automaticly creating fine, if book was returned late
DELIMITER //
CREATE TRIGGER tr_calculate_fine_after_return
AFTER UPDATE ON borrows
FOR EACH ROW
BEGIN
    DECLARE v_days_late INT;
    DECLARE v_fine_per_day DECIMAL(10, 2) DEFAULT 5.00;

    IF NEW.return_date IS NOT NULL AND NEW.return_date > OLD.due_date THEN
        SET v_days_late = DATEDIFF(NEW.return_date, OLD.due_date);
        IF v_days_late > 0 THEN
            INSERT INTO fines (borrow_id, amount)
            VALUES (NEW.id, v_days_late * v_fine_per_day);
        END IF;
    END IF;

    IF NEW.return_date IS NOT NULL THEN
        UPDATE books SET is_available = TRUE WHERE id = NEW.book_id;
    END IF;
END //
DELIMITER ;

-- 11. test datas
INSERT INTO authors (name) VALUES ('Franz Kafka'), ('Fyodor Dostoevsky'), ('J.K. Rowling');
INSERT INTO categories (name) VALUES ('Fiction'), ('Philosophy'), ('Fantasy');
INSERT INTO users (full_name, email, password, role) VALUES ('Admin User', 'admin@library.com', 'admin123', 'ADMIN');

-- book without authors
INSERT INTO books (title, category_id) VALUES ('The Metamorphosis', 1), ('Crime and Punishment', 2);

-- connecting books and authors
-- 1 book (Metamorphosis) -> 1 author (Kafka)
INSERT INTO book_authors (book_id, author_id) VALUES (1, 1);
-- 2 book (Crime and Punishment) -> 2 author (Dostoevsky)
INSERT INTO book_authors (book_id, author_id) VALUES (2, 2);