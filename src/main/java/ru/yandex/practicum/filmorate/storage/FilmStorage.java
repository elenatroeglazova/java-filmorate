package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {

    Film create(Long key, Film value);

    Film update(Long key, Film newValue);

    Film delete(Object key);

    Film read(Object key);

    Collection<Film> findAll();

    Set<Long> ids();

    void clear();
}
