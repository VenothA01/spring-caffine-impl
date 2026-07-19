package com.example.springcaffineimpl.gcs;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GcsFileControllerTest {

	private final HttpClient httpClient = HttpClient.newHttpClient();

	private final ObjectMapper objectMapper = new ObjectMapper();

	@LocalServerPort
	private int port;

	@MockitoBean
	private Storage storage;

	@MockitoBean
	private Blob blob;

	@Test
	void returnsFileFromGoogleCloudStorage() throws Exception {
		byte[] content = "hello from gcs".getBytes(StandardCharsets.UTF_8);
		when(storage.get("test-bucket", "guide.txt")).thenReturn(blob);
		when(blob.exists()).thenReturn(true);
		when(blob.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);
		when(blob.getContent()).thenReturn(content);

		HttpResponse<byte[]> response = httpClient.send(request("/api/files?name=guide.txt").GET().build(),
			HttpResponse.BodyHandlers.ofByteArray());

		assertEquals(200, response.statusCode());
		assertEquals(MediaType.TEXT_PLAIN_VALUE, response.headers().firstValue("content-type").orElseThrow());
		assertArrayEquals(content, response.body());
	}

	@Test
	void missingFileReturnsNotFound() throws Exception {
		when(storage.get("test-bucket", "missing.txt")).thenReturn(null);

		HttpResponse<String> response = httpClient.send(request("/api/files?name=missing.txt").GET().build(),
			HttpResponse.BodyHandlers.ofString());

		assertEquals(404, response.statusCode());
		JsonNode body = objectMapper.readTree(response.body());
		assertEquals("File not found", body.get("title").asText());
		assertEquals("File not found in Google Cloud Storage: missing.txt", body.get("detail").asText());
	}

	private HttpRequest.Builder request(String path) {
		return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
	}

}
