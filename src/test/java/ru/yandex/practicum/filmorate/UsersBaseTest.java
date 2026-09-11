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
public class UsersBaseTest extends BaseTest {
    protected static User baseUser;

    @BeforeAll
    public void createFilm() throws IOException, InterruptedException {
        baseUser = User.builder()
                .email("kinogolik@email.ru")
                .login("kinogolik")
                .name("Котик")
                .birthday(LocalDate.of(2005, 8, 13))
                .build();
        String jsonBody = objectMapper.writeValueAsString(baseUser);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
    }
}
