package com.example.web_bansach.module.author.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.author.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    public Author findByAuthorName(String authorName);

    Page<Author> findByAuthorNameContainingIgnoreCase(String authorName, Pageable pageable);
}



