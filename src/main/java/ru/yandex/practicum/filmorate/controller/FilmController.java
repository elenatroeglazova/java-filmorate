package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation_groups.CreateSequence;
import ru.yandex.practicum.filmorate.validation_groups.UpdateSequence;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/{filmId}")
    public Film film(@PathVariable
                               @Positive(message = "id фильма должен быть положительным числом") Long filmId) {
        return filmService.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
    }

    @GetMapping
    public Collection<Film> films() {
        return filmService.films();
    }

    @GetMapping("/popular")
    public Collection<Film> mostPopular(@RequestParam(defaultValue = "10")
                                        @Positive(message = "Количество фильмов должно быть больше 0") int count,
                                        @RequestParam(required = false) Long genreId,
                                        @RequestParam(required = false) Integer year) {
        return filmService.getMostPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getByDirector(
            @PathVariable @Positive(message = "id режиссера должен быть положительным") Long directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        log.info("Получен запрос на фильмы режиссёра с id={}, сортировка: {}", directorId, sortBy);
        return filmService.getByDirectorId(directorId, sortBy);
    }

    @GetMapping("/common")
    public Collection<Film> common(@RequestParam
                                        @Positive(message = "id пользователя должен быть положительным числом") long userId,
                                        @RequestParam
                                        @Positive(message = "id друга должен быть положительным числом") long friendId) {
        return filmService.common(userId, friendId);
    }

    @PostMapping
    public Film create(@Validated(CreateSequence.class) @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Validated(UpdateSequence.class) @RequestBody Film filmUpdate) {
        return filmService.update(filmUpdate);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable
                       @Positive(message = "id фильма должен быть положительным числом") Long filmId) {
        filmService.delete(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable
                     @Positive(message = "id фильма должен быть положительным числом") Long id,
                     @PathVariable
                     @Positive(message = "id пользователя должен быть положительным числом") Long userId) {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable
                           @Positive(message = "id фильма должен быть положительным числом") Long id,
                           @PathVariable
                           @Positive(message = "id пользователя должен быть положительным числом") Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        return filmService.search(query, by);
    }
}
