package com.example.springcaffineimpl.gcs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GcsControllerAdvice {

	@ExceptionHandler(GcsFileNotFoundException.class)
	public ProblemDetail handleFileNotFound(GcsFileNotFoundException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problemDetail.setTitle("File not found");
		return problemDetail;
	}

}
