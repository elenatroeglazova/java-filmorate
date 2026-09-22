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
import java.util.ArrayList;
import java.util.List;

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
    protected static List<User> testUsers = new ArrayList<>();
    protected static List<Film> testFilms = new ArrayList<>();

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
        for (int i = 0; i < 6; i++) {
            User user = User.builder()
                    .email("user" + i + "@email.ru")
                    .login("user1")
                    .name("Пользователь Один")
                    .birthday(LocalDate.of(2015, 8, 13))
                    .build();
            String jsonBody = objectMapper.writeValueAsString(user);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/users"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .build();

            client.send(req, HttpResponse.BodyHandlers.ofString(UTF_8));
            testUsers.add(user);
        }
    }

    protected void createFilms() throws IOException, InterruptedException {
        postFilm(Film.builder()
                .name("Астрал")
                .releaseDate(LocalDate.of(2011, 4, 1))
                .duration(ofMinutes(102))
                .description("Семья переезжает в новый дом, но вскоре их сын впадает в кому, а его тело становится " +
                        "порталом для злых духов из потустороннего мира.")
                .build());

        postFilm(Film.builder()
                .name("Изгоняющий дьявола")
                .releaseDate(LocalDate.of(1973, 12, 26))
                .duration(ofMinutes(122))
                .description("Мать обращается за помощью к священникам, когда её дочь-подросток начинает проявлять " +
                        "жуткие признаки демонической одержимости.")
                .build());

        postFilm(Film.builder()
                .name("Техасская резня бензопилой")
                .releaseDate(LocalDate.of(1974, 10, 1))
                .duration(ofMinutes(83))
                .description("Группа друзей становится жертвами семьи каннибалов в Техасе, психопата с бензопилой " +
                        "по кличке Кожаное Лицо.")
                .build());

        postFilm(Film.builder()
                .name("Сияние")
                .releaseDate(LocalDate.of(1980, 5, 23))
                .duration(ofMinutes(146))
                .description("Писатель устраивается смотрителем в горный отель на зиму, где из-за изоляции и " +
                        "сверхъестественных сил его безумие приводит к насилию.")
                .build());
    }

    private void postFilm (Film film) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(film);

        HttpRequest postReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl()  + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        client.send(postReq, HttpResponse.BodyHandlers.ofString(UTF_8));
        testFilms.add(film);
    }
}
