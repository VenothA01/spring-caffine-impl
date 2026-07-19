package com.example.springcaffineimpl.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

	private final HttpClient httpClient = HttpClient.newHttpClient();

	private final ObjectMapper objectMapper = new ObjectMapper();

	@LocalServerPort
	private int port;

	@Test
	void missingBookReturnsNotFound() throws Exception {
		HttpResponse<String> response = httpClient.send(request("/api/books/9999").GET().build(), HttpResponse.BodyHandlers.ofString());

		assertEquals(404, response.statusCode());
		JsonNode body = objectMapper.readTree(response.body());
		assertEquals("Book not found", body.get("title").asText());
		assertEquals("Book not found with id 9999", body.get("detail").asText());
	}

	@Test
	void createUpdateAndDeleteBook() throws Exception {
		HttpResponse<String> createResponse = httpClient.send(request("/api/books")
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.POST(HttpRequest.BodyPublishers.ofString("""
					{
					  "title": "Clean Code",
					  "author": "Robert C. Martin"
					}
					"""))
				.build(), HttpResponse.BodyHandlers.ofString());

		assertEquals(201, createResponse.statusCode());
		JsonNode createdBookJson = objectMapper.readTree(createResponse.body());
		long id = createdBookJson.get("id").asLong();
		assertEquals("Clean Code", createdBookJson.get("title").asText());

		HttpResponse<String> updateResponse = httpClient.send(request("/api/books/" + id)
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.PUT(HttpRequest.BodyPublishers.ofString("""
					{
					  "title": "Clean Code Updated",
					  "author": "Robert C. Martin"
					}
					"""))
				.build(), HttpResponse.BodyHandlers.ofString());

		assertEquals(200, updateResponse.statusCode());
		JsonNode updatedBookJson = objectMapper.readTree(updateResponse.body());
		assertEquals(id, updatedBookJson.get("id").asLong());
		assertEquals("Clean Code Updated", updatedBookJson.get("title").asText());

		HttpResponse<String> deleteResponse = httpClient.send(request("/api/books/" + id)
				.DELETE()
				.build(), HttpResponse.BodyHandlers.ofString());
		assertEquals(204, deleteResponse.statusCode());

		HttpResponse<String> getDeletedResponse = httpClient.send(request("/api/books/" + id)
				.GET()
				.build(), HttpResponse.BodyHandlers.ofString());
		assertEquals(404, getDeletedResponse.statusCode());
		assertTrue(getDeletedResponse.body().contains("Book not found with id " + id));
	}

	@Test
	void invalidBookReturnsBadRequest() throws Exception {
		HttpResponse<String> response = httpClient.send(request("/api/books")
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.POST(HttpRequest.BodyPublishers.ofString("""
					{
					  "title": "",
					  "author": ""
					}
					"""))
				.build(), HttpResponse.BodyHandlers.ofString());

		assertEquals(400, response.statusCode());
		JsonNode body = objectMapper.readTree(response.body());
		assertEquals("Validation failed", body.get("title").asText());
		assertEquals("Request validation failed", body.get("detail").asText());
		assertEquals("title is required", body.get("errors").get("title").asText());
		assertEquals("author is required", body.get("errors").get("author").asText());
	}

	private HttpRequest.Builder request(String path) {
		return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
			.header("Accept", MediaType.APPLICATION_JSON_VALUE);
	}

}
