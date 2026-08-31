package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;

public abstract class BaseTest {
    protected static final String BASE = "http://localhost:8080";
    protected static HttpClient client;
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected static Film baseFilm;

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        objectMapper.registerModule(new JavaTimeModule());
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        createFilm();
    }

    public static void createFilm() throws IOException, InterruptedException {
        baseFilm = Film.builder()
                .name("Астрал")
                .releaseDate(LocalDate.of(2011, 4, 1))
                .duration(ofMinutes(102))
                .description("Семья переезжает в новый дом, но вскоре их сын впадает в кому, а его тело становится " +
                        "порталом для злых духов из потустороннего мира.")
                .build();
        String jsonBody = objectMapper.writeValueAsString(baseFilm);

        HttpRequest postReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(postReq, HttpResponse.BodyHandlers.ofString(UTF_8));
    }
}
