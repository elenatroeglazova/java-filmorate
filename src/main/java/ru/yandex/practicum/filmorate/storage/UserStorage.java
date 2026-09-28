package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(User value);

    User update(Long key, User newValue);

    User delete(Object key);

    User read(Long key);

    Collection<User> findAll();

    void clear();
}
