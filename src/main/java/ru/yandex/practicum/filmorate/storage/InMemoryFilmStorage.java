package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.RatingMPA;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.util.*;

@Component
@Qualifier("inMemoryStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Genre> genres = new HashMap<>();

    @Override
    public Film create(Film film) {
        Long key = IdGenerator.getNextId(films.keySet());
        film.setId(key);
        film.getGenres().forEach(g -> genres.put(g.getId(), g));
        return films.put(key, film);
    }

    @Override
    public void update(Film filmUpdate) {
        Film currentFilm = films.get(filmUpdate.getId());

        currentFilm.setDescription(filmUpdate.getDescription());
        currentFilm.setName(filmUpdate.getName());
        currentFilm.setReleaseDate(filmUpdate.getReleaseDate());
        currentFilm.setDuration(filmUpdate.getDuration());
        currentFilm.setGenres(filmUpdate.getGenres());

        filmUpdate.getGenres().forEach(g -> genres.put(g.getId(), g));
    }

    @Override
    public boolean delete(Long id) {
        return films.remove(id) != null;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(Long id) {
        return findAll().stream()
                .filter(f -> f.getId().equals(id))
                .findAny();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        findById(filmId).ifPresent(f -> f.getLikes().add(userId));
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        return findById(filmId).stream()
                .peek(f -> f.getLikes().remove(userId))
                .noneMatch(f -> f.getLikes().contains(userId));
    }

    @Override
    public Collection<Genre> genres() {
        return genres.values();
    }

    @Override
    public Collection<Rating> ratings() {
        return Arrays.stream(RatingMPA.values())
                .map(mpa -> Rating.builder()
                        .id((long) (mpa.ordinal() + 1))
                        .name(mpa)
                        .build())
                .toList();
    }

    @Override
    public Genre findGenreById(Long genreId) {
        return genres.get(genreId);
    }

    @Override
    public Rating findRatingById(Long ratingId) {
        return Arrays.stream(RatingMPA.values())
                .filter(mpa -> mpa.ordinal() == ratingId - 1)
                .map(mpa -> Rating.builder()
                        .id((long) (mpa.ordinal() + 1))
                        .name(mpa)
                        .build())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Не найден рейтинг по id=" + ratingId));
    }

    @Override
    public void clear() {
        films.clear();
    }
}
