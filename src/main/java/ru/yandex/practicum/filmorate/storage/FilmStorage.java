package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film value);

    Film update(Long key, Film newValue);

    Film delete(Long key);

    Collection<Film> findAll();

    Optional<Film> findById(Long key);

    void clear();
}
