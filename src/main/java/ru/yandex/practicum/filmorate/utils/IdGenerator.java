package ru.yandex.practicum.filmorate.utils;

import java.util.Set;

public class IdGenerator {
    public static long getNextId(Set<Long> ids) {
        long currentMaxId = ids
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
