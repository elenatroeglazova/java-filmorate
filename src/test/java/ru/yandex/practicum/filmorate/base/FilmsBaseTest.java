package ru.yandex.practicum.filmorate.base;

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
    protected Film baseFilm;

    @BeforeAll
    public void setUp() throws IOException, InterruptedException {
        baseFilm = Film.builder()
                .name("Кошмар на улице Вязов")
                .releaseDate(LocalDate.of(1984, 11, 9))
                .duration(ofMinutes(91))
                .description("Подростки из одного района сталкиваются с Фредди Крюгером — маньяком с перчаткой-лезвием, " +
                        "который убивает своих жертв в их снах.")
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
