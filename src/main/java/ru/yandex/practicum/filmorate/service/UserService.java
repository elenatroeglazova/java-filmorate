package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage users = new InMemoryUserStorage();

    public Collection<User> users() {
        log.info("Получен запрос на получение всех пользователей. Всего пользователей: {}", users.size());
        log.debug("Список пользователей: {}", users.values());
        return users.values();
    }

    public Optional<User> getById(Long id) {
        log.debug("Поиск пользователя по id={}", id);
        Optional<User> user = users.values().stream()
                .filter(u -> u.getId().equals(id))
                .findAny();
        user.ifPresentOrElse(
                u -> log.debug("Пользователь найден: {}", u),
                () -> log.debug("Пользователь с id={} не найден", id)
        );
        return user;
    }

    public User create(User user) {
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

    public User update(User userUpdate) {
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
        throw new NotFoundException("Пользователь с id = " + userUpdate.getId() + " не найден");
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Получен запрос на добавление в друзья для пользователей с id={} и id={}", friendId, userId);

        if (userId == null || friendId == null) {
            log.error("Id пользователя или друга не может быть пустым: userId={}, friendId={}", userId, friendId);
            throw new ValidationException("Id пользователя не может быть пустым");
        }

        boolean isUserExist = users.containsKey(userId);
        boolean isFriendExist = users.containsKey(friendId);

        log.trace("Пользователь есть в сторадже: {}, друг есть в сторадже: {}", isUserExist, isFriendExist);

        if (isUserExist && isFriendExist) {
            log.trace("Список друзей до добавления: {}", users.get(userId).getFriends());

            boolean isAdded = users.get(userId).getFriends().add(friendId);

            log.trace("Список друзей после добавления: {}", users.get(userId).getFriends());
            if (isAdded) {
                log.info("Пользователь {} успешно добавил в друзья пользователя {}", userId, friendId);
            } else {
                throw new IllegalStateException("Не удалось добавить в друзья по неизвестной причине");
            }

            isAdded = users.get(friendId).getFriends().add(userId);

            log.trace("Список друзей после добавления: {}", users.get(userId).getFriends());
            if (isAdded) {
                log.info("Пользователь {} успешно добавил в друзья пользователя {}", friendId, userId);
            } else {
                throw new IllegalStateException("Не удалось добавить в друзья по неизвестной причине");
            }
        } else {
            if (!isUserExist) {
                log.error("Попытка добавить друга несуществующему пользователю с id={}", userId);
                throw new NotFoundException("Пользователя с id=" + userId + " не существует");
            }

            log.error("Попытка добавить несуществующего пользователя с id={} в друзья", friendId);
            throw new NotFoundException("Пользователя с id=" + friendId + " не существует");
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Получен запрос на удаление друга с id={} из списка друзей пользователя с id={}", friendId, userId);

        if (userId == null || friendId == null) {
            log.error("Id пользователя или друга не может быть пустым: userId={}, friendId={}", userId, friendId);
            throw new ValidationException("Id пользователя/друга не может быть пустым");
        }

        boolean isUserExist = users.containsKey(userId);
        boolean isFriendExist = users.containsKey(friendId);

        log.trace("Пользователь есть в сторадже: {}, друг есть в сторадже: {}", isUserExist, isFriendExist);

        if (isUserExist && isFriendExist) {
            log.trace("Список друзей: {}", users.get(userId).getFriends());
            log.trace("Друг есть в списке друзей: {}", users.get(userId).getFriends().contains(friendId));

            boolean isFriendRemoved = users.get(userId).getFriends().remove(friendId);
            if (isFriendRemoved) {
                log.info("Пользователь {} успешно удалил из друзей пользователя {}", userId, friendId);
            } else {
                log.warn("Друг с id={} не найден в списке друзей пользователя с id={}", friendId, userId);
            }

            log.trace("Список друзей: {}", users.get(friendId).getFriends());
            log.trace("Друг есть в списке друзей: {}", users.get(friendId).getFriends().contains(userId));

            isFriendRemoved = users.get(friendId).getFriends().remove(userId);
            if (isFriendRemoved) {
                log.info("Пользователь {} успешно удалил из друзей пользователя {}", friendId, userId);
            } else {
                log.warn("Друг с id={} не найден в списке друзей пользователя с id={}", userId, friendId);
            }

            log.trace("Список друзей после удаления: {}", users.get(userId).getFriends());
        } else {
            if (!isUserExist) {
                log.error("Попытка удаления из друзей не существующего пользователя с id={}", userId);
                throw new NotFoundException("Пользователь с id=" + userId + " не существует");
            }

            log.error("Попытка удаления из друзей не существующего пользователя с id={}", friendId);
            throw new NotFoundException("Пользователь с id=" + friendId + " не существует");
        }
    }

    public Collection<User> friends(Long id) {
        log.info("Получен запрос на получение списка друзей пользователя с id={}", id);

        if (id == null) {
            log.error("Id пользователя не может быть пустым: userId={}", id);
            throw new ValidationException("Id пользователя не может быть пустым");
        }

        if (users.containsKey(id)) {
            User user = users.get(id);

            log.debug("Пользователь: {}, список друзей: {}", user, user.getFriends());
            return user.getFriends().stream()
                    .map(users::get)
                    .collect(Collectors.toSet());
        }

        log.error("Попытка получить список друзей несуществующего пользователя с id={}", id);
        throw new NotFoundException("Пользователь с id = " + id + " не найден");
    }

    public Collection<User> mutualFriends(Long userId, Long otherId) {
        log.info("Получен запрос на просмотр общих друзей пользователей с id={}, {}", userId, otherId);

        if (userId == null || otherId == null) {
            throw new ValidationException("Id пользователей для просмотра общего списка друзей не могут быть пустыми");
        }

        boolean isUserExist = users.containsKey(userId);
        boolean isOtherExist = users.containsKey(otherId);

        log.trace("Пользователь есть в сторадже: {}, другой пользователь есть в сторадже: {}",
                isUserExist, isOtherExist);

        if (isUserExist && isOtherExist) {
            Set<Long> intersection = new HashSet<>(users.get(userId).getFriends());
            intersection.retainAll(users.get(otherId).getFriends());
            return intersection.stream()
                    .map(users::get)
                    .collect(Collectors.toSet());
        }
        log.error("Попытка получить список общих друзей для несуществующего(их) пользователя(ей) с id={}, {}",
                userId, otherId);
        throw new NotFoundException("Передан несуществующий(ие) пользователь(ли)");
    }
}
