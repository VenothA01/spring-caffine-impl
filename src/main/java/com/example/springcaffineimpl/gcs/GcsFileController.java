package com.example.springcaffineimpl.gcs;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class GcsFileController {

	private final GcsFileService gcsFileService;

	public GcsFileController(GcsFileService gcsFileService) {
		this.gcsFileService = gcsFileService;
	}

	@GetMapping
	public ResponseEntity<byte[]> getFile(@RequestParam("name") String fileName) {
		GcsFileResponse file = gcsFileService.getFile(fileName);
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(file.contentType()))
			.header(HttpHeaders.CONTENT_DISPOSITION,
				ContentDisposition.inline().filename(file.fileName()).build().toString())
			.body(file.content());
	}

}
