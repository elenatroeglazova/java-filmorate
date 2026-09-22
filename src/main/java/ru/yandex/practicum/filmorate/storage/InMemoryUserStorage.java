package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public int size() {
        return users.size();
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return users.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return users.containsValue(value);
    }

    @Override
    public User get(Object key) {
        return users.get(key);
    }

    @Override
    public User put(Long key, User value) {
        return users.put(key, value);
    }

    @Override
    public User remove(Object key) {
        return users.remove(key);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends User> m) {
        users.putAll(m);
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public Set<Long> keySet() {
        return users.keySet();
    }

    @Override
    public Collection<User> values() {
        return users.values();
    }

    @Override
    public Set<Entry<Long, User>> entrySet() {
        return users.entrySet();
    }
}
