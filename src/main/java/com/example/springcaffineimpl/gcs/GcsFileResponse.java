package com.example.springcaffineimpl.gcs;

public record GcsFileResponse(String fileName, String contentType, byte[] content) {
}
