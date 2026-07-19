package com.example.springcaffineimpl.gcs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.gcs")
public record GcsProperties(String bucketName) {
}
