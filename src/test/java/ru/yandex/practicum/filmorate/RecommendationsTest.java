package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.base.RecommendationsBaseTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class RecommendationsTest extends RecommendationsBaseTest {
    @Test
    void getRecommendationsTest() throws IOException, InterruptedException {
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/1/recommendations"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertThat(resp.statusCode())
                .as("GET /users/1/recommendations должен вернуть 200")
                .isEqualTo(200);

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertThat(contentTypeHeaderValue)
                .as("Content-Type должен содержать формат данных и кодировку")
                .isEqualTo("application/json");

        String body = resp.body().trim();
        List<Film> recommendations = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(recommendations.stream().map(Film::getName).toList())
                .as("Рекомендации фильмов для пользователя с id=1")
                .withFailMessage("Ответ должен содержать 2 рекомендации - rec_film_2 и rec_film_10")
                .isNotEmpty()
                .containsExactlyInAnyOrder("rec_film_2", "rec_film_10")
                .hasSize(2);
    }

    @Test
    void getRecommendationsWithoutLikesMatchesTest() throws IOException, InterruptedException {
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/6/recommendations"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertThat(resp.statusCode())
                .as("GET /users/6/recommendations должен вернуть 200")
                .isEqualTo(200);

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertThat(contentTypeHeaderValue)
                .as("Content-Type должен содержать формат данных и кодировку")
                .isEqualTo("application/json");

        String body = resp.body().trim();
        List<Film> recommendations = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(recommendations.stream().map(Film::getName).toList())
                .as("Рекомендации фильмов для пользователя с id=6")
                .withFailMessage("Ответ должен содержать пустой список")
                .isEmpty();
    }

    @Test
    void getRecommendationsForUserWithoutLikesTest() throws IOException, InterruptedException {
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/7/recommendations"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertThat(resp.statusCode())
                .as("GET /users/7/recommendations должен вернуть 200")
                .isEqualTo(200);

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertThat(contentTypeHeaderValue)
                .as("Content-Type должен содержать формат данных и кодировку")
                .isEqualTo("application/json");

        String body = resp.body().trim();
        List<Film> recommendations = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(recommendations.stream().map(Film::getName).toList())
                .as("Рекомендации фильмов для пользователя с id=7")
                .withFailMessage("Ответ должен содержать пустой список")
                .isEmpty();
    }

    @Test
    void getRecommendationsForUserWithFullyMatchingLikesTest() throws IOException, InterruptedException {
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/8/recommendations"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertThat(resp.statusCode())
                .as("GET /users/8/recommendations должен вернуть 200")
                .isEqualTo(200);

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertThat(contentTypeHeaderValue)
                .as("Content-Type должен содержать формат данных и кодировку")
                .isEqualTo("application/json");

        String body = resp.body().trim();
        List<Film> recommendations = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(recommendations.stream().map(Film::getName).toList())
                .as("Рекомендации фильмов для пользователя с id=8")
                .withFailMessage("Ответ должен содержать пустой список")
                .isEmpty();
    }
}
