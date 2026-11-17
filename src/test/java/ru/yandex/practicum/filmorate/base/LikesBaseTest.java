package ru.yandex.practicum.filmorate.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class LikesBaseTest extends BaseTest {

    @BeforeAll
    protected void setUp() {
        createUsers();
        createFilms();
        addLikes();
    }

    private void addLikes() {
        filmStorage.addLike(2L, 1L);
        filmStorage.addLike(3L, 1L);
        filmStorage.addLike(3L, 2L);
        filmStorage.addLike(3L, 3L);
        filmStorage.addLike(3L, 4L);
        filmStorage.addLike(3L, 5L);
        filmStorage.addLike(3L, 6L);
        filmStorage.addLike(4L, 6L);
        filmStorage.addLike(4L, 5L);
        filmStorage.addLike(4L, 4L);
    }
}
