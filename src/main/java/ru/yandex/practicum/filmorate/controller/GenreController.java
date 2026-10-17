package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@Validated
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final FilmService filmService;

    @GetMapping()
    public Collection<Genre> genres() {
        return filmService.genres();
    }

    @GetMapping("/{id}")
    public Genre genre(@PathVariable
                                 @Positive(message = "id жанра должен быть положительным числом") Long id) {
        return filmService.findGenreById(id);
    }
}
