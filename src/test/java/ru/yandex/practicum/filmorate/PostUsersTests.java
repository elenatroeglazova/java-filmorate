package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostUsersTests extends BaseTest {

    @Test
    public void postUserTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "POST /users должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        User respUser = objectMapper.readValue(resp.body().trim(), User.class);
        user.setId(respUser.getId());
        assertEquals(user, respUser, "В ответе должны быть данные пользователя из запроса");
    }

    @Test
    void postUserWithEmptyBodyTest() throws IOException, InterruptedException {
        User user = User.builder().build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void postUserWithNullEmailTest() throws IOException, InterruptedException {
        User user = User.builder()
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void postUserWithBlankEmailTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void postUserWithEmailWithoutAtSignTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil_mail")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void postUserWithBlankLoginTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .login("")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void postUserWithNullLoginTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void postUserWithSpacesInLoginTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .login("kinofil 00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void postUserWithBirthdayAfterTodayTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.now().plusDays(15))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "POST /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    public void postUserWithNullNameTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .login("kinofil00")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "POST /users должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        User respUser = objectMapper.readValue(resp.body().trim(), User.class);
        user.setId(respUser.getId());
        user.setName(user.getLogin());
        assertEquals(user, respUser, "В ответе должны быть данные пользователя из запроса с логином в имени");
    }

    @Test
    public void postUserWithBlankNameTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "POST /users должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        User respUser = objectMapper.readValue(resp.body().trim(), User.class);
        user.setId(respUser.getId());
        user.setName(user.getLogin());
        assertEquals(user, respUser, "В ответе должны быть данные пользователя из запроса с логином в имени");
    }
}
