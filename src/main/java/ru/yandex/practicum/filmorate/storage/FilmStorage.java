package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findByDirectorId(Long directorId, String sortBy);


    Film create(Film film);

    void update(Film filmUpdate);

    boolean delete(Long id);

    Collection<Film> findAll();

    Collection<Film> findByIds(Collection<Long> filmIds);

    Optional<Film> findById(Long id);

    void addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Film> mostPopular(int count, Long genreId, Integer year);

    void clear();

    List<Film> search(String query, String by);

    List<Long> common(Long userId, Long friendId);
}
