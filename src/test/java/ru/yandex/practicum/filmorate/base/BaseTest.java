package ru.yandex.practicum.filmorate.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static HttpClient client;
    protected static User user1;
    protected static User user2;
    protected static User user3;
    protected static User user4;
    protected static User user5;
    protected static User user6;
    protected static Film film1;
    protected static Film film2;
    protected static Film film3;
    protected static Film film4;

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    protected void createUsers() throws IOException, InterruptedException {
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

    protected void createFilms() throws IOException, InterruptedException {
        film1 = Film.builder()
                .name("Астрал")
                .releaseDate(LocalDate.of(2011, 4, 1))
                .duration(ofMinutes(102))
                .description("Семья переезжает в новый дом, но вскоре их сын впадает в кому, а его тело становится " +
                        "порталом для злых духов из потустороннего мира.")
                .build();
        String jsonBody = objectMapper.writeValueAsString(film1);

        HttpRequest postReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl()  + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(postReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        film2 = Film.builder()
                .name("Изгоняющий дьявола")
                .releaseDate(LocalDate.of(1973, 12, 26))
                .duration(ofMinutes(122))
                .description("Мать обращается за помощью к священникам, когда её дочь-подросток начинает проявлять " +
                        "жуткие признаки демонической одержимости.")
                .build();
        jsonBody = objectMapper.writeValueAsString(film2);

        postReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl()  + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(postReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        film3 = Film.builder()
                .name("Техасская резня бензопилой")
                .releaseDate(LocalDate.of(1974, 10, 1))
                .duration(ofMinutes(83))
                .description("Группа друзей становится жертвами семьи каннибалов в Техасе, психопата с бензопилой " +
                        "по кличке Кожаное Лицо.")
                .build();
        jsonBody = objectMapper.writeValueAsString(film3);

        postReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl()  + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(postReq, HttpResponse.BodyHandlers.ofString(UTF_8));

        film4 = Film.builder()
                .name("Сияние")
                .releaseDate(LocalDate.of(1980, 5, 23))
                .duration(ofMinutes(146))
                .description("Писатель устраивается смотрителем в горный отель на зиму, где из-за изоляции и " +
                        "сверхъестественных сил его безумие приводит к насилию.")
                .build();
        jsonBody = objectMapper.writeValueAsString(film4);

        postReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl()  + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(postReq, HttpResponse.BodyHandlers.ofString(UTF_8));
    }
}
