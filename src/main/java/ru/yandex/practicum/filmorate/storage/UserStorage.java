package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    User create(Long key, User value);

    User update(Long key, User newValue);

    User delete(Object key);

    User read(Object key);

    Collection<User> findAll();

    Set<Long> ids();

    void clear();
}
