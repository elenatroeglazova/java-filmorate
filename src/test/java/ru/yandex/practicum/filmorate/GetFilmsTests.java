package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.base.FilmsBaseTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetFilmsTests extends FilmsBaseTest {
    @Test
    void getFilmsListTest() throws IOException, InterruptedException {
        Set<Film> expectedFilms = new HashSet<>();
        film1.setId(1L);
        film2.setId(2L);
        film3.setId(3L);
        film4.setId(4L);
        expectedFilms.add(film1);
        expectedFilms.add(film2);
        expectedFilms.add(film3);
        expectedFilms.add(film4);

        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "GET /films должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = resp.body().trim();
        Set<Film> films = objectMapper.readValue(body, new TypeReference<>() {});

        assertEquals(expectedFilms, films);
    }
}
