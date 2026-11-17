package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.base.BaseTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonFilmsTest extends BaseTest {

    @BeforeAll
    public void setUp() {
        createUsers();
        createFilms();

        userStorage.addFriend(5L, 6L);
        filmStorage.addLike(1L, 5L);
        filmStorage.addLike(2L, 5L);
        filmStorage.addLike(3L, 5L);
        filmStorage.addLike(2L, 6L);
        filmStorage.addLike(3L, 6L);
        filmStorage.addLike(4L, 6L);

        userStorage.addFriend(1L, 3L);
        filmStorage.addLike(1L, 1L);
        filmStorage.addLike(2L, 1L);
        filmStorage.addLike(3L, 3L);
        filmStorage.addLike(4L, 3L);
    }

    @Test
    public void getCommonFilmsTest() throws IOException, InterruptedException {
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/common?userId=5&friendId=6"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "GET /films/common?userId=5&friendId=6 должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = resp.body().trim();
        Set<Film> films = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(films.stream().map(Film::getName).toList())
                .as("Список общих фильмов")
                .isNotEmpty()
                .containsExactlyInAnyOrder("Изгоняющий дьявола", "Техасская резня бензопилой")
                .hasSize(2);
    }

    @Test
    public void noCommonFilmsTest() throws IOException, InterruptedException {
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/common?userId=1&friendId=3"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "GET /films/common?userId=1&friendId=3 должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = resp.body().trim();
        Set<Film> films = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(films.stream().map(Film::getName).toList())
                .as("Список общих фильмов")
                .isEmpty();
    }
}
