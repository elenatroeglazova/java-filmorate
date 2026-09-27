package ru.yandex.practicum.filmorate.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    FilmService filmService;

    protected static HttpClient client;
    protected static List<User> testUsers = new ArrayList<>();
    protected static List<Film> testFilms = new ArrayList<>();
    protected UserStorage userStorage;
    protected FilmStorage filmStorage;

    @BeforeAll
    protected void beforeAll() {
        filmStorage = filmService.getFilms();
        userStorage = userService.getUsers();
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    protected void createUsers() {

        for (int i = 0; i < 6; i++) {
            Long id = IdGenerator.getNextId(userStorage.ids());
            User user = User.builder()
                    .id(id)
                    .email("user" + i + "@email.ru")
                    .login("user1")
                    .name("Пользователь Один")
                    .birthday(LocalDate.of(2015, 8, 13))
                    .build();

            userStorage.create(id, user);
            testUsers.add(user);
        }
    }

    protected void createFilms() {
        storageFilm(Film.builder()
                .name("Астрал")
                .releaseDate(LocalDate.of(2011, 4, 1))
                .duration(ofMinutes(102))
                .description("Семья переезжает в новый дом, но вскоре их сын впадает в кому, а его тело становится " +
                        "порталом для злых духов из потустороннего мира.")
                .build());

        storageFilm(Film.builder()
                .name("Изгоняющий дьявола")
                .releaseDate(LocalDate.of(1973, 12, 26))
                .duration(ofMinutes(122))
                .description("Мать обращается за помощью к священникам, когда её дочь-подросток начинает проявлять " +
                        "жуткие признаки демонической одержимости.")
                .build());

        storageFilm(Film.builder()
                .name("Техасская резня бензопилой")
                .releaseDate(LocalDate.of(1974, 10, 1))
                .duration(ofMinutes(83))
                .description("Группа друзей становится жертвами семьи каннибалов в Техасе, психопата с бензопилой " +
                        "по кличке Кожаное Лицо.")
                .build());

        storageFilm(Film.builder()
                .name("Сияние")
                .releaseDate(LocalDate.of(1980, 5, 23))
                .duration(ofMinutes(146))
                .description("Писатель устраивается смотрителем в горный отель на зиму, где из-за изоляции и " +
                        "сверхъестественных сил его безумие приводит к насилию.")
                .build());
    }

    private void storageFilm(Film film) {
        Long id = IdGenerator.getNextId(filmStorage.ids());
        film.setId(id);
        filmStorage.create(id, film);
        testFilms.add(film);
    }

    @AfterAll
    public void tearDown() {
        filmStorage.clear();
        userStorage.clear();
    }
}
