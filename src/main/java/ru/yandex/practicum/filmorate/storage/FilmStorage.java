package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    void update(Film filmUpdate);

    boolean delete(Long id);

    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    void addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Genre> genres();

    Collection<Rating> ratings();

    Genre findGenreById(Long genreId);

    Rating findRatingById(Long ratingId);

    void clear();
}
