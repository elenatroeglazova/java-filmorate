package ru.yandex.practicum.filmorate.base;

import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static java.time.Duration.ofMinutes;

public class RecommendationsBaseTest extends BaseTest {
    @BeforeAll
    protected void setUp() {
        prepareRecommendationsTestData();
    }

    private void prepareRecommendationsTestData() {
        for (int i = 1; i <= 12; i++) {
            filmStorage.create(Film.builder()
                    .name("rec_film_" + i)
                    .releaseDate(LocalDate.now())
                    .duration(ofMinutes(120))
                    .description("rec film description: " + i)
                    .mpa(rating)
                    .build());
        }

        for (int i = 1; i <= 9; i++) {
            userStorage.create(User.builder()
                    .email("rec_user" + i + "@email.ru")
                    .login("rec_user" + i)
                    .name("Пользователь")
                    .birthday(LocalDate.now())
                    .build());
        }

        filmStorage.addLike(1L, 1L);
        filmStorage.addLike(3L, 1L);
        filmStorage.addLike(5L, 1L);
        filmStorage.addLike(7L, 1L);
        filmStorage.addLike(9L, 1L);

        filmStorage.addLike(1L, 2L);
        filmStorage.addLike(2L, 2L);
        filmStorage.addLike(3L, 2L);
        filmStorage.addLike(5L, 2L);

        filmStorage.addLike(10L, 3L);
        filmStorage.addLike(9L, 3L);
        filmStorage.addLike(7L, 3L);
        filmStorage.addLike(5L, 3L);

        filmStorage.addLike(1L, 4L);
        filmStorage.addLike(9L, 4L);
        filmStorage.addLike(4L, 4L);

        filmStorage.addLike(4L, 5L);
        filmStorage.addLike(7L, 5L);
        filmStorage.addLike(8L, 5L);

        filmStorage.addLike(11L, 6L);

        filmStorage.addLike(12L, 8L);

        filmStorage.addLike(12L, 9L);
    }
}
