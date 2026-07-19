package com.example.springcaffineimpl.gcs;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GcsProperties.class)
public class GcsConfig {

	@Bean
	Storage storage() {
		return StorageOptions.getDefaultInstance().getService();
	}

}
