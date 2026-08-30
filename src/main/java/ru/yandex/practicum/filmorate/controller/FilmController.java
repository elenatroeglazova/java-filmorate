package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> films() {
        log.info("Возвращает список фильмов");
        log.debug("Список фильмов: {}", films.values());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Добавляет новый фильм");
        log.debug("""
                        Данные нового фильма:
                        Название фильма {}
                        Описание фильма {}
                        Дата выпуска фильма {}
                        Продолжительность фильма {}""",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может содержать больше 200 символов");
        }

        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new ValidationException("Фильм не может быть выпущен раньше " + FIRST_FILM_RELEASE);
        }

        if (film.getDuration().isNegative()) {
            throw new ValidationException("Продолжиьельность фильма не может быть отрицательным числом");
        }

        film.setId(IdGenerator.getNextId(films.keySet()));
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film filmUpdate) {
        log.info("Обновляет данные фильма");
        log.debug("""
                        Данные для обновления фильма:
                        Название фильма {}
                        Описание фильма {}
                        Дата выпуска фильма {}
                        Продолжительность фильма {}""",
                filmUpdate.getName(), filmUpdate.getDescription(),
                filmUpdate.getReleaseDate(), filmUpdate.getDuration());
        if (filmUpdate.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(filmUpdate.getId())) {
            log.debug("Найден фильм по id");
            Film currentFilm = films.get(filmUpdate.getId());
            log.debug("""
                            Данные найденного фильма:
                            Название фильма {}
                            Описание фильма {}
                            Дата выпуска фильма {}
                            Продолжительность фильма {}""",
                    currentFilm.getName(), currentFilm.getDescription(),
                    currentFilm.getReleaseDate(), currentFilm.getDuration());
            if (filmUpdate.getName() == null || filmUpdate.getReleaseDate() == null ||
                    filmUpdate.getDescription() == null || filmUpdate.getDuration() == null) {
                log.warn("Отсутствуют некоторые обязательные данные. Возвращает найденный фильм");
                return currentFilm;
            }

            if (filmUpdate.getDescription().length() > 200) {
                throw new ValidationException("Описание не может содержать больше 200 символов");
            }

            if (filmUpdate.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
                throw new ValidationException("Фильм не может быть выпущен раньше " + FIRST_FILM_RELEASE);
            }

            if (filmUpdate.getDuration().isNegative()) {
                throw new ValidationException("Продолжиьельность фильма не может быть отрицательным числом");
            }

            currentFilm.setDescription(filmUpdate.getDescription());
            currentFilm.setName(filmUpdate.getName());
            currentFilm.setReleaseDate(filmUpdate.getReleaseDate());
            currentFilm.setDuration(filmUpdate.getDuration());
            log.info("Данные фильма обновлены");
            return currentFilm;
        }
        throw new NotFoundException("Фильм с id = " + filmUpdate.getId() + " не найден");
    }
}
