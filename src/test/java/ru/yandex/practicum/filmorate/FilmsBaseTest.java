package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class FilmsBaseTest extends BaseTest {
    protected static Film baseFilm;

    @BeforeAll
    public void createFilm() throws IOException, InterruptedException {
        baseFilm = Film.builder()
                .name("Астрал")
                .releaseDate(LocalDate.of(2011, 4, 1))
                .duration(ofMinutes(102))
                .description("Семья переезжает в новый дом, но вскоре их сын впадает в кому, а его тело становится " +
                        "порталом для злых духов из потустороннего мира.")
                .build();
        String jsonBody = objectMapper.writeValueAsString(baseFilm);

        HttpRequest postReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl()  + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(postReq, HttpResponse.BodyHandlers.ofString(UTF_8));
    }
}
