package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.base.FriendsBaseTest;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class UserFriendsTests extends FriendsBaseTest {

    @Test
    public void addFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends/4"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, resp.statusCode(), "PUT /users/3/friends/4 должен вернуть 200");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        String body = resp.body().trim();
        List<User> actualFriends = objectMapper.readValue(body, new TypeReference<>() {});
        User newFriend = actualFriends.stream()
                .filter(fr -> fr.getId().equals(4L))
                .findAny()
                .orElse(null);

        assertEquals(200, resp.statusCode(), "GET /users/3/friends должен вернуть 200");
        assertNotNull(newFriend, "В списке друзей пользователя с id=3 должен появиться пользователь с id=4");
        assertTrue(newFriend.getFriends().contains(3L),
                "В списке друзей пользователя с id=4 должен быть друг с id=3");
    }

    @Test
    public void addUnknownIdFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends/999"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "PUT /users/3/friends/999 должен вернуть 404");

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/3/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "GET /users/3/friends должен вернуть 200");

        String body = resp.body().trim();
        List<User> actualFriends = objectMapper.readValue(body, new TypeReference<>() {});

        assertTrue(actualFriends.isEmpty() || actualFriends.stream().noneMatch(u -> u.getId().equals(7L)),
                "В списке друзей пользователя с id=3 не должно быть друга с id=7");
    }

    @Test
    public void addFriendForUnknownUserTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/999/friends/3"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, resp.statusCode(), "PUT /users/999/friends/3 должен вернуть 404");
    }

    @Test
    public void getFriendsTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/1/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "GET /users/1/friends должен вернуть 200");

        String body = resp.body().trim();
        List<User> actualFriends = objectMapper.readValue(body, new TypeReference<>() {});
        User friend = actualFriends.stream().filter(fr -> fr.getId().equals(2L)).findAny().orElse(null);

        assertFalse(actualFriends.isEmpty(), "Должен быть получен список друзей");
        assertNotNull(friend, "В списке друзей пользователя с id=1 должен быть пользователь с id=2");
    }

    @Test
    public void getUnknownIdFriendsTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/999/friends"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, resp.statusCode(), "GET /users/999/friends должен вернуть 404");
    }

    @Test
    public void removeFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/6"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "DELETE /users/5/friends/6 должен вернуть 200");

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
                .uri(URI.create(getBaseUrl() + "/users/5/friends/999"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, resp.statusCode(), "DELETE /users/5/friends/999 должен вернуть 404");
    }

    @Test
    public void removeFriendForUnknownUserTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/999/friends/5"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, resp.statusCode(), "DELETE /users/999/friends/5 должен вернуть 404");
    }

    @Test
    public void removeUnknownFriendTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/3"))
                .DELETE()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, resp.statusCode(), "DELETE /users/5/friends/3 должен вернуть 200");
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
                "GET /users/5/friends/common/6 должен вернуть 200");
        String body = resp.body().trim();
        List<User> mutualFriend = objectMapper.readValue(body, new TypeReference<>() {});

        assertFalse(mutualFriend.isEmpty(),
                "Список общих друзей пользователей с id=5 и id=6 не должен быть пустым");
        assertEquals(1, mutualFriend.size(), "У пользователей с id=5 и id=6 только один общий друг");
        assertEquals(1L, mutualFriend.getFirst().getId(),
                "Id общего друга пользователей с id=5 и id=6 должен равняться 1");
    }

    @Test
    public void getMutualFriendsWithUnknownUserTest() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users/5/friends/common/999"))
                .GET()
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, resp.statusCode(),
                "GET /users/5/friends/common/999 должен вернуть 404");
    }
}
