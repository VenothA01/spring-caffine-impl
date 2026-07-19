# spring-caffine-impl

Spring Boot 4 sample application that combines:

- a CRUD API for books
- PostgreSQL persistence with Spring Data JPA
- Caffeine caching for book lookups
- Google Cloud Storage file retrieval

## Tech Stack

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Caffeine Cache
- Google Cloud Storage client
- Maven

## Features

- `GET /api/books/{id}` is cached with the `books` cache
- cache entries are updated on create and update, and evicted on delete
- sample book data is inserted on startup when the database is empty
- `GET /api/files?name=...` reads a file from a configured GCS bucket
- validation and not-found errors are returned as `ProblemDetail` responses

## Configuration

Default application properties are in `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spring_caffine_impl
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.cache.type=caffeine
spring.cache.cache-names=books
spring.cache.caffeine.spec=maximumSize=500,expireAfterAccess=10m
app.gcs.bucket-name=
```

### GCS setup

Set `app.gcs.bucket-name` to your bucket name.

The application creates the `Storage` client through `StorageOptions.getDefaultInstance()`, so authentication follows Google Application Default Credentials. Common local setup:

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account.json"
```

## Running Locally

1. Start PostgreSQL and create a database named `spring_caffine_impl`.
2. Update database credentials in `src/main/resources/application.properties` if needed.
3. Set `app.gcs.bucket-name` if you want to use the file endpoint.
4. Start the app:

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080` by default.

## Tests

```bash
./mvnw test
```

Tests use H2 from `src/test/resources/application.properties`.

## API

### Books

List books:

```bash
curl http://localhost:8080/api/books
```

Get a book by id:

```bash
curl http://localhost:8080/api/books/1
```

Create a book:

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert C. Martin"}'
```

Update a book:

```bash
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Effective Java","author":"Joshua Bloch"}'
```

Delete a book:

```bash
curl -X DELETE http://localhost:8080/api/books/1
```

Book payload rules:

- `title` is required and must be at most 100 characters
- `author` is required and must be at most 100 characters

When the database is empty, these records are seeded on startup:

- `Spring in Action` by `Craig Walls`
- `Effective Java` by `Joshua Bloch`

### Files

Fetch a file from GCS by object name:

```bash
curl "http://localhost:8080/api/files?name=documents/sample.pdf" --output sample.pdf
```

If the object exists, the response content type is taken from GCS and returned inline.

## Project Structure

- `src/main/java/com/example/springcaffineimpl/book` - book domain, API, cache integration
- `src/main/java/com/example/springcaffineimpl/gcs` - GCS configuration, service, API
- `src/main/resources/application.properties` - runtime configuration
- `src/test` - application and controller tests
