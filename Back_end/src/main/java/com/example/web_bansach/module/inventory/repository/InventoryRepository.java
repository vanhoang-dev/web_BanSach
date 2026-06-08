package com.example.web_bansach.module.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.inventory.entity.Inventory;

import jakarta.persistence.LockModeType;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	// Find inventory record by book id
	java.util.Optional<Inventory> findByBookId(Long bookId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i FROM Inventory i WHERE i.book.id = :bookId")
	java.util.Optional<Inventory> findByBookIdForUpdate(@Param("bookId") Long bookId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i FROM Inventory i WHERE i.id = :id")
	java.util.Optional<Inventory> findByIdForUpdate(@Param("id") Long id);

}



