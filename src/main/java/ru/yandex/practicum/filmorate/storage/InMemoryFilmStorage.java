package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public int size() {
        return films.size();
    }

    @Override
    public boolean isEmpty() {
        return films.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return films.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return films.containsValue(value);
    }

    @Override
    public Film get(Object key) {
        return films.get(key);
    }

    @Override
    public Film put(Long key, Film value) {
        return films.put(key, value);
    }

    @Override
    public Film remove(Object key) {
        return films.remove(key);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Film> m) {
        films.putAll(m);
    }

    @Override
    public void clear() {
        films.clear();
    }

    @Override
    public Set<Long> keySet() {
        return films.keySet();
    }

    @Override
    public Collection<Film> values() {
        return films.values();
    }

    @Override
    public Set<Entry<Long, Film>> entrySet() {
        return films.entrySet();
    }
}
