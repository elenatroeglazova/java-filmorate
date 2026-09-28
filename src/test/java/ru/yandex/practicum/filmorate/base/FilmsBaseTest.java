package ru.yandex.practicum.filmorate.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class FilmsBaseTest extends BaseTest {
    protected Film baseFilm;

    @BeforeAll
    public void setUp() {
        baseFilm = Film.builder()
                .name("Кошмар на улице Вязов")
                .releaseDate(LocalDate.of(1984, 11, 9))
                .duration(ofMinutes(91))
                .description("Подростки из одного района сталкиваются с Фредди Крюгером — маньяком с перчаткой-лезвием, " +
                        "который убивает своих жертв в их снах.")
                .build();

        filmStorage.create(baseFilm);
    }
}
