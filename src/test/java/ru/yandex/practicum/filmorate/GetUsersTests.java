package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetUsersTests extends UsersBaseTest {
    @Test
    void getUsersListTest() throws IOException, InterruptedException {
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "GET /users должен вернуть 200");

        String contentTypeHeaderValue = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = resp.body().trim();
        List<User> users = objectMapper.readValue(body, new TypeReference<>() {});

        boolean shouldBeKinogolikInTheList = users.stream()
                .anyMatch(f -> f.getLogin().equals(baseUser.getLogin()));
        assertTrue(shouldBeKinogolikInTheList, "В списке должен появиться фильм " + baseUser);
    }
}
