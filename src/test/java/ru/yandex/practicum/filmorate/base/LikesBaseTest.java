package ru.yandex.practicum.filmorate.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class LikesBaseTest extends BaseTest {

    @BeforeAll
    protected void setUp() throws IOException, InterruptedException {
        createUsers();
        createFilms();
        addLikes();
    }

    private void addLikes() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/2/like/1"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/3/like/1"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/3/like/2"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/3/like/3"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/3/like/4"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/3/like/5"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/3/like/6"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/4/like/6"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/4/like/5"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));

        req = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/films/4/like/4"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
    }
}
