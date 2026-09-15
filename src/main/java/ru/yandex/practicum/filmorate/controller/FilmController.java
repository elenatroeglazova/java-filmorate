package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation_groups.CreateSequence;
import ru.yandex.practicum.filmorate.validation_groups.UpdateSequence;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/{filmId}")
    public Optional<Film> film(@PathVariable Long filmId) {
        return filmService.getById(filmId);
    }

    @GetMapping
    public Collection<Film> films() {
        return filmService.films();
    }

    @GetMapping("/popular")
    public Collection<Film> mostPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostPopular(count);
    }

    @PostMapping
    public Film create(@Validated(CreateSequence.class) @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Validated(UpdateSequence.class) @RequestBody Film filmUpdate) {
        return filmService.update(filmUpdate);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id,
                     @PathVariable Long userId) {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void dislike(@PathVariable Long id,
                        @PathVariable Long userId) {
        filmService.dislike(id, userId);
    }
}
