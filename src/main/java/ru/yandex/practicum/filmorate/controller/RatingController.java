package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@Validated
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {
    private final FilmService filmService;

    @GetMapping()
    public Collection<Rating> ratings() {
        return filmService.ratings();
    }

    @GetMapping("/{id}")
    public Rating rating(@PathVariable
                         @Positive(message = "id рейтинга должен быть положительным числом") Long id) {
        return filmService.findRatingById(id);
    }
}
