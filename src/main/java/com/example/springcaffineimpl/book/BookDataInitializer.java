package com.example.springcaffineimpl.book;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BookDataInitializer implements CommandLineRunner {

	private final BookService bookService;

	public BookDataInitializer(BookService bookService) {
		this.bookService = bookService;
	}

	@Override
	public void run(String... args) {
		if (bookService.count() > 0) {
			return;
		}

		bookService.saveAll(List.of(
			new Book("Spring in Action", "Craig Walls"),
			new Book("Effective Java", "Joshua Bloch")
		));
	}

}
