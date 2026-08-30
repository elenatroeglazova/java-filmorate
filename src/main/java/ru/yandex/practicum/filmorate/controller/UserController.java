package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static LocalDate CURRENT_DATE = LocalDate.now();
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> users() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта пользователя не должна быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта пользователя должна содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getBirthday().isAfter(CURRENT_DATE)) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(IdGenerator.getNextId(users.keySet()));
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User userUpdate) {
        if (userUpdate.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(userUpdate.getId())) {
            User currentUser = users.get(userUpdate.getId());
            if (userUpdate.getLogin() == null || userUpdate.getEmail() == null || userUpdate.getBirthday() == null) {
                return userUpdate;
            }

            if (userUpdate.getEmail().isBlank()) {
                throw new ValidationException("Электронная почта пользователя не должна быть пустой");
            }

            if (!userUpdate.getEmail().contains("@")) {
                throw new ValidationException("Электронная почта пользователя должна содержать @");
            }

            if (userUpdate.getLogin().isBlank()) {
                throw new ValidationException("Логин не может быть пустым");
            }

            if (userUpdate.getLogin().contains(" ")) {
                throw new ValidationException("Логин не должен содержать пробелы");
            }

            if (userUpdate.getBirthday().isAfter(CURRENT_DATE)) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }

            if (userUpdate.getName() == null || userUpdate.getName().isBlank()) {
                userUpdate.setName(userUpdate.getLogin());
            }

            currentUser.setEmail(userUpdate.getEmail());
            currentUser.setLogin(userUpdate.getLogin());
            currentUser.setName(userUpdate.getName());
            currentUser.setBirthday(userUpdate.getBirthday());
            return currentUser;
        }
        throw new NotFoundException("Фильм с id = " + userUpdate.getId() + " не найден");
    }
}
