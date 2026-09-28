package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film value);

    Film update(Long key, Film newValue);

    Film delete(Long key);

    Film read(Object key);

    Collection<Film> findAll();

    void clear();
}
