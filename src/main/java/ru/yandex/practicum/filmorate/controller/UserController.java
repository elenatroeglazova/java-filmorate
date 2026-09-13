package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.utils.IdGenerator;
import ru.yandex.practicum.filmorate.validation_groups.CreateSequence;
import ru.yandex.practicum.filmorate.validation_groups.UpdateSequence;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage users = new InMemoryUserStorage();

    @GetMapping
    public Collection<User> users() {
        log.info("Получен запрос на получение всех пользователей. Всего пользователей: {}", users.size());
        log.debug("Список пользователей: {}", users.values());
        return users.values();
    }

    @PostMapping
    public User create(@Validated(CreateSequence.class) @RequestBody User user) {
        log.info("Получен запрос на создание пользователя с логином '{}'", user.getLogin());
        log.debug("Данные нового пользователя: \nemail {}\n логин {}\n имя пользователя {}\nдень рождения {}",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя пустое, будет использован логин '{}'", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(IdGenerator.getNextId(users.keySet()));
        users.put(user.getId(), user);
        log.info("Пользователь создан: id={}, логин='{}'", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Validated(UpdateSequence.class) @RequestBody User userUpdate) {
        log.info("Получен запрос на обновление пользователя с id={}", userUpdate.getId());
        log.debug("Данные обновляемого пользователя: \nemail {}\n логин {}\n имя пользователя {}\nдень рождения {}",
                userUpdate.getEmail(), userUpdate.getLogin(), userUpdate.getName(), userUpdate.getBirthday());

        if (users.containsKey(userUpdate.getId())) {
            log.debug("Пользователь найден среди уже существующих по его id");
            User currentUser = users.get(userUpdate.getId());
            log.debug("Данные найденного пользователя: \nemail {}\n логин {}\n имя пользователя {}\nдень рождения {}",
                    currentUser.getEmail(), currentUser.getLogin(), currentUser.getName(), currentUser.getBirthday());

            if (userUpdate.getName() == null || userUpdate.getName().isBlank()) {
                log.warn("Имя пользователя пустое, будет использован логин '{}'", userUpdate.getLogin());
                userUpdate.setName(userUpdate.getLogin());
            }

            currentUser.setEmail(userUpdate.getEmail());
            currentUser.setLogin(userUpdate.getLogin());
            currentUser.setName(userUpdate.getName());
            currentUser.setBirthday(userUpdate.getBirthday());
            log.info("Пользователь с id={} обновлен, новый логин='{}'", userUpdate.getId(), userUpdate.getLogin());
            return currentUser;
        }

        log.error("Попытка обновить несуществующего пользователя с id={}", userUpdate.getId());
        throw new NotFoundException("Фильм с id = " + userUpdate.getId() + " не найден");
    }
}
