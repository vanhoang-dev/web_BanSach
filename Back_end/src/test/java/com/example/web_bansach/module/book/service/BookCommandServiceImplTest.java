package com.example.web_bansach.module.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.mapper.BookMapper;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.book.service.impl.BookCommandServiceImpl;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;

@ExtendWith(MockitoExtension.class)
class BookCommandServiceImplTest {

    @Mock private BookRepository bookRepository;
    @Mock private BookMapper bookMapper;
    @Mock private BookValidationService bookValidationService;
    @Mock private BookAssemblyService bookAssemblyService;
    @Mock private InventoryRepository inventoryRepository;

    @InjectMocks
    private BookCommandServiceImpl bookCommandService;

    @Test
    void createBook_shouldCreateEmptyInventory() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code");
        request.setIsbn("ISBN-1");
        request.setPublisher("NXB");
        request.setPublicationYear(2024);
        request.setPrice(new BigDecimal("100000"));
        request.setAuthorId(1L);
        request.setCategoryId(1L);

        Book assembledBook = new Book();
        assembledBook.setTitle("Clean Code");

        when(bookAssemblyService.assembleBookFromRequest(request, null)).thenReturn(assembledBook);
        when(bookRepository.save(assembledBook)).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setId(10L);
            return book;
        });

        bookCommandService.createBook(request, null);

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());
        assertThat(captor.getValue().getBook().getId()).isEqualTo(10L);
        assertThat(captor.getValue().getQuantity()).isZero();
    }
}
