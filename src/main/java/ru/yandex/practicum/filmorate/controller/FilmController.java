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

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> films() {
        return filmService.films();
    }

    @PostMapping
    public Film create(@Validated(CreateSequence.class) @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Validated(UpdateSequence.class) @RequestBody Film filmUpdate) {
        return filmService.update(filmUpdate);
    }
}
