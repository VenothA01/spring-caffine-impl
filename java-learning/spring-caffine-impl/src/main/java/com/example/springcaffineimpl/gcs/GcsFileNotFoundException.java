package com.example.springcaffineimpl.gcs;

public class GcsFileNotFoundException extends RuntimeException {

	public GcsFileNotFoundException(String fileName) {
		super("File not found in Google Cloud Storage: " + fileName);
	}

}
