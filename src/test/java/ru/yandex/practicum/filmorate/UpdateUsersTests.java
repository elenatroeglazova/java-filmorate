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

public class UpdateUsersTests extends UsersBaseTest {

    @Test
    public void updateUserTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "PUT /users должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        User respUser = objectMapper.readValue(resp.body().trim(), User.class);
        assertEquals(user, respUser, "В ответе должны быть данные пользователя из запроса");
    }

    @Test
    void updateByUserWithoutIdTest() throws IOException, InterruptedException {
        User user = User.builder()
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithUnknownIdTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(10000L)
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithEmptyBodyTest() throws IOException, InterruptedException {
        User user = User.builder().build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithNullEmailTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithBlankEmailTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithEmailWithoutAtSignTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil_mail")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithBlankLoginTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil@email.ru")
                .login("")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithNullLoginTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil@email.ru")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithSpacesInLoginTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil@email.ru")
                .login("kinofil 00")
                .name("Dolly")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    void updateByUserWithBirthdayAfterTodayTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("Dolly")
                .birthday(LocalDate.now().plusDays(15))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(500, resp.statusCode(), "PUT /users должен вернуть 500");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");
    }

    @Test
    public void updateByUserWithNullNameTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil@email.ru")
                .login("kinofil00")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "PUT /users должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        User respUser = objectMapper.readValue(resp.body().trim(), User.class);
        user.setId(respUser.getId());
        user.setName(user.getLogin());
        assertEquals(user, respUser, "В ответе должны быть данные пользователя из запроса с логином в имени");
    }

    @Test
    public void updateByUserWithBlankNameTest() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("kinofil@email.ru")
                .login("kinofil00")
                .name("")
                .birthday(LocalDate.of(2000, 5, 7))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "PUT /users должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        User respUser = objectMapper.readValue(resp.body().trim(), User.class);
        user.setId(respUser.getId());
        user.setName(user.getLogin());
        assertEquals(user, respUser, "В ответе должны быть данные пользователя из запроса с логином в имени");
    }
}
