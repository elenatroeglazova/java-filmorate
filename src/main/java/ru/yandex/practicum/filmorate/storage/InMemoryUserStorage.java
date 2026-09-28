package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User read(Long key) {
        return users.get(key);
    }

    @Override
    public User create(User value) {
        Long key = IdGenerator.getNextId(users.keySet());
        value.setId(key);
        return users.put(key, value);
    }

    @Override
    public User update(Long key, User newValue) {
        User currentUser = users.get(key);

        currentUser.setEmail(newValue.getEmail());
        currentUser.setLogin(newValue.getLogin());
        currentUser.setName(newValue.getName());
        currentUser.setBirthday(newValue.getBirthday());
        return currentUser;
    }

    @Override
    public User delete(Object key) {
        return users.remove(key);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void clear() {
        users.clear();
    }
}
