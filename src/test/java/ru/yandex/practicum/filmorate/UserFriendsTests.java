package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class UserFriendsTests extends FriendsBaseTest {

    @Test
    public void addFriendTest() throws IOException, InterruptedException {
        List<User> expectedFriends = new ArrayList<>();
        user4.setId(4L);
        user4.getFriends().add(3L);
        expectedFriends.add(user4);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends/4"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "PUT /users/{id}/friends/{friendId} должен вернуть 200");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        String body = resp.body().trim();
        List<User> actualFriends = objectMapper.readValue(body, new TypeReference<>() {});

        assertEquals(200, resp.statusCode(), "GET /users/{id}/friends должен вернуть 200");
        assertEquals(expectedFriends, actualFriends,
                "В списке друзей пользователя с id=3 должен появиться пользователь с id=4");
        assertTrue(actualFriends.getFirst().getFriends().contains(3L),
                "В списке друзей пользователя с id=4 должен быть друг с id=3");
    }

    @Test
    public void addUnknownIdFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends/7"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "PUT /users/{id}/friends/{friendId} должен вернуть 404");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "GET /users/{id}/friends должен вернуть 200");

        String body = resp.body().trim();
        List<User> actualFriends = objectMapper.readValue(body, new TypeReference<>() {});

        assertTrue(actualFriends.isEmpty() || actualFriends.stream().noneMatch(u -> u.getId().equals(7L)),
                "В списке друзей пользователя с id=3 не должно быть друга с id=7");
    }

    @Test
    public void addFriendForUnknownUserTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/7/friends/3"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "PUT /users/{id}/friends/{friendId} должен вернуть 404");
    }

    @Test
    public void getFriendsTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/1/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "GET /users/{id}/friends должен вернуть 200");

        String body = resp.body().trim();
        List<User> actualFriends = objectMapper.readValue(body, new TypeReference<>() {});
        user2.setId(2L);
        user2.getFriends().add(1L);

        assertEquals(user2, actualFriends.getFirst(),
                "В списке друзей пользователя с id=1 должен быть пользователь с id=2");
        assertTrue(actualFriends.getFirst().getFriends().contains(1L),
                "В списке друзей пользователя с id=2 должен быть друг с id=1");
    }

    @Test
    public void getUnknownIdFriendsTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/7/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, resp.statusCode(), "GET /users/{id}/friends должен вернуть 404");
    }

    @Test
    public void removeFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/6"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "DELETE /users/{id}/friends/{friendId} должен вернуть 200");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        String body = resp.body().trim();
        List<User> friends5 = objectMapper.readValue(body, new TypeReference<>() {});

        assertTrue(friends5.isEmpty() || friends5.stream().noneMatch(u -> u.getId().equals(6L)),
                "У пользователя с id=5 не должно быть в друзьях пользователя с id=6");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/6/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        body = resp.body().trim();
        List<User> friends6 = objectMapper.readValue(body, new TypeReference<>() {});

        assertTrue(friends6.isEmpty() || friends6.stream().noneMatch(u -> u.getId().equals(5L)),
                "У пользователя с id=6 не должно быть в друзьях пользователя с id=5");
    }

    @Test
    public void removeUnknownIdFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/7"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, resp.statusCode(), "DELETE /users/{id}/friends/{friendId} должен вернуть 404");
    }

    @Test
    public void removeFriendForUnknownUserTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/7/friends/5"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, resp.statusCode(), "DELETE /users/{id}/friends/{friendId} должен вернуть 404");
    }

    @Test
    public void removeUnknownFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/3"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "DELETE /users/{id}/friends/{friendId} должен вернуть 200");
    }

    @Test
    public void getMutualFriendsTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/common/6"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(),
                "GET /users/{id}/friends/common/{otherId} должен вернуть 200");
        String body = resp.body().trim();
        List<User> mutualFriend = objectMapper.readValue(body, new TypeReference<>() {});

        assertFalse(mutualFriend.isEmpty(),
                "Список общих друзей пользователей с id=5 и id=6 не должен быть пустым");
        assertEquals(1, mutualFriend.size(), "У пользователей с id=5 и id=6 только один общий друг");
        assertEquals(1L, mutualFriend.getFirst().getId(),
                "Id общего друга пользователей с id=5 и id=6 должен равняться 1");
    }
}
