package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

public abstract class BaseTest {
    protected static final String BASE = "http://localhost:8080";
    protected static HttpClient client;
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        objectMapper.registerModule(new JavaTimeModule());
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }
}
