package com.example.springcaffineimpl.gcs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;

import org.springframework.stereotype.Service;

@Service
public class GcsFileService {

	private final Storage storage;
	private final GcsProperties gcsProperties;

	public GcsFileService(Storage storage, GcsProperties gcsProperties) {
		this.storage = storage;
		this.gcsProperties = gcsProperties;
	}

	public GcsFileResponse getFile(String fileName) {
		Blob blob = storage.get(gcsProperties.bucketName(), fileName);
		if (blob == null || !blob.exists()) {
			throw new GcsFileNotFoundException(fileName);
		}

		String contentType = blob.getContentType();
		return new GcsFileResponse(fileName, contentType == null ? "application/octet-stream" : contentType,
			blob.getContent());
	}

}
