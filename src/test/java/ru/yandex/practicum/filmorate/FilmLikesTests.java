package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.base.LikesBaseTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class FilmLikesTests extends LikesBaseTest {

    @Test
    public void addLikeTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/1/like/1"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "PUT /films/1/like/1 должен вернуть 200");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        String body = resp.body().trim();
        List<Film> films = objectMapper.readValue(body, new TypeReference<>() {});
        Set<Long> likes = films.stream()
                .filter(f -> f.getId().equals(1L))
                .findFirst()
                .map(Film::getLikes)
                .orElse(new HashSet<>());
        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(1L);

        assertFalse(likes.isEmpty(), "Список лайков фильма с id=1 не должен быть пустым");
        assertEquals(1, likes.size(), "Количество лайков фильма с id=1 должно быть равно 1");
        assertEquals(expectedLikes, likes,
                "Список лайков фильма с id=1 должен содержать лайк пользователя с id=1");
    }

    @Test
    public void addLikeToUnknownFilmTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/999/like/1"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "PUT /films/999/like/1 должен вернуть 404");
    }

    @Test
    public void addLikeFromUnknownUserTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/1/like/999"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "PUT /films/1/like/999 должен вернуть 404");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        String body = resp.body().trim();
        List<Film> films = objectMapper.readValue(body, new TypeReference<>() {});
        Set<Long> likes = films.stream()
                .filter(f -> f.getId().equals(1L))
                .findFirst()
                .map(Film::getLikes)
                .orElse(new HashSet<>());

        assertTrue(likes.isEmpty() || !likes.contains(7L),
                "Список лайков фильма с id=1 не должен содержать лайк от пользователя с id=7");
    }

    @Test
    public void removeLikeTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/2/like/1"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "DELETE /films/2/like/1 должен вернуть 200");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        String body = resp.body().trim();
        List<Film> films = objectMapper.readValue(body, new TypeReference<>() {});
        Set<Long> likes = films.stream()
                .filter(f -> f.getId().equals(2L))
                .findFirst()
                .map(Film::getLikes)
                .orElse(new HashSet<>());

        assertTrue(likes.isEmpty() || !likes.contains(1L),
                "Список лайков фильма с id=2 не должен содержать лайк от пользователя с id=1");
    }

    @Test
    public void removeLikeForUnknownFilmTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/999/like/1"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "DELETE /films/999/like/1 должен вернуть 404");
    }

    @Test
    public void removeLikeFromUnknownUserTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/2/like/999"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "DELETE /films/2/like/999 должен вернуть 404");
    }

    @Test
    public void getMostPopularTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/popular?count=2"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "GET /films/popular должен вернуть 200");

        String body = resp.body().trim();
        List<Film> mostPopularFilms = objectMapper.readValue(body, new TypeReference<>() {});

        assertEquals(2, mostPopularFilms.size(),
                "Список самых популярных фильмов должен состоять из 2 фильмов");
        assertEquals(film3.getName(), mostPopularFilms.getFirst().getName(),
                "Самым популярным в списке должен быть фильм с id=3");
        assertEquals(film4.getName(), mostPopularFilms.getLast().getName(),
                "Вторым по популярности в списке должен бьть фильм с id=4");
    }

    @Test
    public void getDefaultMostPopularTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/popular"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "GET /films/popular должен вернуть 200");

        String body = resp.body().trim();
        List<Film> mostPopularFilms = objectMapper.readValue(body, new TypeReference<>() {});

        assertEquals(4, mostPopularFilms.size(),
                "Список самых популярных фильмов должен состоять из 4 фильмов");
        assertEquals(film3.getName(), mostPopularFilms.getFirst().getName(),
                "Самым популярным в списке должен быть фильм с id=3");
        assertEquals(film1.getName(), mostPopularFilms.getLast().getName(),
                "Последним по популярности в списке должен бьть фильм с id=1");
    }
}
