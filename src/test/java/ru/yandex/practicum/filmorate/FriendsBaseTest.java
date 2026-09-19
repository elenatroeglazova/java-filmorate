package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class FriendsBaseTest extends BaseTest {
    protected static User user1;
    protected static User user2;
    protected static User user3;
    protected static User user4;
    protected static User user5;
    protected static User user6;

    @BeforeAll
    protected void setUp() throws IOException, InterruptedException {
        createUsers();
        addFriends();
    }

    private void createUsers() throws IOException, InterruptedException {
        user1 = User.builder()
                .email("user1@email.ru")
                .login("user1")
                .name("Пользователь Один")
                .birthday(LocalDate.of(2015, 8, 13))
                .build();
        String jsonBody = objectMapper.writeValueAsString(user1);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        user2 = User.builder()
                .email("user2@email.ru")
                .login("user2")
                .name("Пользователь Два")
                .birthday(LocalDate.of(2017, 4, 3))
                .build();
        jsonBody = objectMapper.writeValueAsString(user2);

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        user3 = User.builder()
                .email("user3@email.ru")
                .login("user3")
                .name("Пользователь Три")
                .birthday(LocalDate.of(2014, 6, 12))
                .build();
        jsonBody = objectMapper.writeValueAsString(user3);

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        user4 = User.builder()
                .email("user4@email.ru")
                .login("user4")
                .name("Пользователь Четыре")
                .birthday(LocalDate.of(2019, 8, 22))
                .build();
        jsonBody = objectMapper.writeValueAsString(user4);

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        user5 = User.builder()
                .email("user5@email.ru")
                .login("user5")
                .name("Пользователь Пять")
                .birthday(LocalDate.of(2019, 8, 22))
                .build();
        jsonBody = objectMapper.writeValueAsString(user5);

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        user6 = User.builder()
                .email("user6@email.ru")
                .login("user6")
                .name("Пользователь Шесть")
                .birthday(LocalDate.of(2019, 8, 22))
                .build();
        jsonBody = objectMapper.writeValueAsString(user6);

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
    }

    private void addFriends() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/1/friends/2"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/6"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/1"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/6/friends/1"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
    }
}
