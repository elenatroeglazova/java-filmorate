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
        filmStorage.read(2L).getLikes().add(1L);
        filmStorage.read(3L).getLikes().add(1L);
        filmStorage.read(3L).getLikes().add(2L);
        filmStorage.read(3L).getLikes().add(3L);
        filmStorage.read(3L).getLikes().add(4L);
        filmStorage.read(3L).getLikes().add(5L);
        filmStorage.read(3L).getLikes().add(6L);
        filmStorage.read(4L).getLikes().add(6L);
        filmStorage.read(4L).getLikes().add(5L);
        filmStorage.read(4L).getLikes().add(4L);
    }
}
