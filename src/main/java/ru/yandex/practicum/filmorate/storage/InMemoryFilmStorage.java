package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film read(Object key) {
        return films.get(key);
    }

    @Override
    public Film create(Film value) {
        Long key = IdGenerator.getNextId(films.keySet());
        value.setId(key);
        return films.put(key, value);
    }

    @Override
    public Film update(Long key, Film newValue) {
        Film currentFilm = films.get(key);

        currentFilm.setDescription(newValue.getDescription());
        currentFilm.setName(newValue.getName());
        currentFilm.setReleaseDate(newValue.getReleaseDate());
        currentFilm.setDuration(newValue.getDuration());
        return currentFilm;
    }

    @Override
    public Film delete(Long key) {
        return films.remove(key);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public void clear() {
        films.clear();
    }
}
