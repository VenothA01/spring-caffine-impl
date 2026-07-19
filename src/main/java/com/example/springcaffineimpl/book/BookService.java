package com.example.springcaffineimpl.book;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public List<Book> findAll() {
		return bookRepository.findAll();
	}

	@Cacheable("books")
	public Book findById(Long id) {
		return bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException(id));
	}

	@CachePut(value = "books", key = "#result.id")
	public Book create(Book book) {
		return bookRepository.save(book);
	}

	@CachePut(value = "books", key = "#id")
	public Book update(Long id, Book book) {
		Book existingBook = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException(id));

		existingBook.setTitle(book.getTitle());
		existingBook.setAuthor(book.getAuthor());
		return bookRepository.save(existingBook);
	}

	@CacheEvict(value = "books", key = "#id")
	public void delete(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException(id));
		bookRepository.delete(book);
	}

	public long count() {
		return bookRepository.count();
	}

	public void saveAll(List<Book> books) {
		bookRepository.saveAll(books);
	}

}
