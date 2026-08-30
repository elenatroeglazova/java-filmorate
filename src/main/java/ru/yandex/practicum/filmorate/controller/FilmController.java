package ru.yandex.practicum.filmorate.controller;

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

    @GetMapping
    public Collection<Film> films() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
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
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film filmUpdate) {
        if (filmUpdate.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(filmUpdate.getId())) {
            Film currentFilm = films.get(filmUpdate.getId());
            if (filmUpdate.getName() == null || filmUpdate.getReleaseDate() == null ||
                    filmUpdate.getDescription() == null || filmUpdate.getDuration() == null) {
                return filmUpdate;
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
            return currentFilm;
        }
        throw new NotFoundException("Фильм с id = " + filmUpdate.getId() + " не найден");
    }
}
