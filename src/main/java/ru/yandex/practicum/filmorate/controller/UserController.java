package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> users() {
        log.info("Возвращает список пользователей");
        log.debug("Список пользователей: {}", users.values());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Добавляет нового пользлвателя");
        log.debug("Данные нового пользователя: \nemail {}\n логин {}\n имя пользователя {}\nдень рождения {}",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Пустой email");
            throw new ValidationException("Электронная почта пользователя не должна быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.debug("Электронная почта пользователя {}", user.getEmail());
            throw new ValidationException("Электронная почта пользователя должна содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.debug("Логин пользователя {}", user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getBirthday().isAfter(CURRENT_DATE)) {
            log.debug("День рождения пользователя {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Пустое имя пользователя");
            user.setName(user.getLogin());
        }

        user.setId(IdGenerator.getNextId(users.keySet()));
        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User userUpdate) {
        log.info("Обновляет данные пользователя");
        log.debug("Данные обновляемого пользователя: \nemail {}\n логин {}\n имя пользователя {}\nдень рождения {}",
                userUpdate.getEmail(), userUpdate.getLogin(), userUpdate.getName(), userUpdate.getBirthday());
        if (userUpdate.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(userUpdate.getId())) {
            log.debug("Пользователь найден среди уже существующих по его id");
            User currentUser = users.get(userUpdate.getId());
            log.debug("Данные найденного пользователя: \nemail {}\n логин {}\n имя пользователя {}\nдень рождения {}",
                    currentUser.getEmail(), currentUser.getLogin(), currentUser.getName(), currentUser.getBirthday());
            if (userUpdate.getLogin() == null || userUpdate.getEmail() == null || userUpdate.getBirthday() == null) {
                log.warn("Отсутствуют некоторрые данные пользователя");
                return currentUser;
            }

            if (userUpdate.getEmail().isBlank()) {
                throw new ValidationException("Электронная почта пользователя не должна быть пустой");
            }

            if (!userUpdate.getEmail().contains("@")) {
                log.debug("Электронная почта пользователя {}", userUpdate.getEmail());
                throw new ValidationException("Электронная почта пользователя должна содержать @");
            }

            if (userUpdate.getLogin().isBlank()) {
                throw new ValidationException("Логин не может быть пустым");
            }

            if (userUpdate.getLogin().contains(" ")) {
                log.debug("Логин пользователя {}", userUpdate.getLogin());
                throw new ValidationException("Логин не должен содержать пробелы");
            }

            if (userUpdate.getBirthday().isAfter(CURRENT_DATE)) {
                log.debug("День рождения пользователя {}", userUpdate.getBirthday());
                throw new ValidationException("Дата рождения не может быть в будущем");
            }

            if (userUpdate.getName() == null || userUpdate.getName().isBlank()) {
                log.warn("Пустое имя пользователя");
                userUpdate.setName(userUpdate.getLogin());
            }

            currentUser.setEmail(userUpdate.getEmail());
            currentUser.setLogin(userUpdate.getLogin());
            currentUser.setName(userUpdate.getName());
            currentUser.setBirthday(userUpdate.getBirthday());
            log.info("Пользователь обновлен");
            return currentUser;
        }

        throw new NotFoundException("Фильм с id = " + userUpdate.getId() + " не найден");
    }
}
