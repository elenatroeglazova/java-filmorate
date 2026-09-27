package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User read(Object key) {
        return users.get(key);
    }

    @Override
    public User create(Long key, User value) {
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
    public Set<Long> ids() {
        return users.keySet();
    }

    @Override
    public void clear() {
        users.clear();
    }
}
