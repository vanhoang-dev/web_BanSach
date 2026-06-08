package com.example.web_bansach.module.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.book.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

        boolean existsByIsbn(String isbn);

        boolean existsByIsbnAndIdNot(String isbn, Long id);

        @Query(value = "SELECT DISTINCT b FROM Book b " +
                        "LEFT JOIN FETCH b.author " +
                        "LEFT JOIN FETCH b.category " +
                        "LEFT JOIN FETCH b.discount " +
                        "WHERE b.deletedAt IS NULL",
                        countQuery = "SELECT COUNT(b) FROM Book b WHERE b.deletedAt IS NULL")
        Page<Book> findAllActiveBooks(Pageable pageable);

        @Query(value = "SELECT DISTINCT b FROM Book b " +
                        "LEFT JOIN FETCH b.author " +
                        "LEFT JOIN FETCH b.category c " +
                        "LEFT JOIN FETCH b.discount " +
                        "WHERE c.id = :categoryId AND b.deletedAt IS NULL",
                        countQuery = "SELECT COUNT(b) FROM Book b WHERE b.category.id = :categoryId AND b.deletedAt IS NULL")
        Page<Book> findByCategoryIdWithJoin(@Param("categoryId") Long categoryId, Pageable pageable);

        @Query(value = "SELECT DISTINCT b FROM Book b " +
                        "LEFT JOIN FETCH b.author " +
                        "LEFT JOIN FETCH b.category " +
                        "LEFT JOIN FETCH b.discount " +
                        "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "AND b.deletedAt IS NULL",
                        countQuery = "SELECT COUNT(b) FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND b.deletedAt IS NULL")
        Page<Book> searchByTitleWithJoin(@Param("keyword") String keyword, Pageable pageable);

        @Query("SELECT b FROM Book b " +
                        "LEFT JOIN FETCH b.author " +
                        "LEFT JOIN FETCH b.category " +
                        "LEFT JOIN FETCH b.discount " +
                        "WHERE b.id = :id")
        Book findByIdWithJoin(@Param("id") Long id);
}
