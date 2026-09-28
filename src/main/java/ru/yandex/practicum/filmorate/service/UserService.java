package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage users;

    public Collection<User> users() {
        log.info("Получен запрос на получение всех пользователей. Всего пользователей: {}", users.findAll().size());
        log.debug("Список пользователей: {}", users.findAll());
        return users.findAll();
    }

    public Optional<User> getById(Long id) {
        log.debug("Поиск пользователя по id={}", id);
        Optional<User> user = users.findAll().stream()
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
        users.create(user);
        log.info("Пользователь создан: id={}, логин='{}'", user.getId(), user.getLogin());
        return user;
    }

    public User update(User userUpdate) {
        log.info("Получен запрос на обновление пользователя с id={}", userUpdate.getId());
        log.debug("Данные обновляемого пользователя: \nemail {}\n логин {}\n имя пользователя {}\nдень рождения {}",
                userUpdate.getEmail(), userUpdate.getLogin(), userUpdate.getName(), userUpdate.getBirthday());

        Long updatedId = userUpdate.getId();
        if (users.read(updatedId) != null) {
            log.debug("Пользователь найден среди уже существующих по его id");

            if (userUpdate.getName() == null || userUpdate.getName().isBlank()) {
                log.warn("Имя пользователя пустое, будет использован логин '{}'", userUpdate.getLogin());
                userUpdate.setName(userUpdate.getLogin());
            }

            User newUser = users.update(updatedId, userUpdate);
            log.info("Пользователь с id={} обновлен, новый логин='{}'", userUpdate.getId(), userUpdate.getLogin());
            return newUser;
        }

        log.error("Попытка обновить несуществующего пользователя с id={}", userUpdate.getId());
        throw new NotFoundException("Пользователь с id = " + userUpdate.getId() + " не найден");
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Получен запрос на добавление в друзья для пользователей с id={} и id={}", friendId, userId);

        boolean isUserExist = users.read(userId) != null;
        boolean isFriendExist = users.read(friendId) != null;

        log.trace("Пользователь есть в сторадже: {}, друг есть в сторадже: {}", isUserExist, isFriendExist);

        if (isUserExist && isFriendExist) {
            log.trace("Список друзей до добавления: {}", users.read(userId).getFriends());

            boolean isAdded = users.read(userId).getFriends().add(friendId);

            log.trace("Список друзей после добавления: {}", users.read(userId).getFriends());
            if (isAdded) {
                log.info("Пользователь {} успешно добавил в друзья пользователя {}", userId, friendId);
            } else {
                throw new IllegalStateException("Не удалось добавить в друзья по неизвестной причине");
            }

            isAdded = users.read(friendId).getFriends().add(userId);

            log.trace("Список друзей после добавления: {}", users.read(userId).getFriends());
            if (isAdded) {
                log.info("Пользователь {} успешно добавил в друзья пользователя {}", friendId, userId);
            } else {
                throw new IllegalStateException("Не удалось добавить в друзья по неизвестной причине");
            }

            return;
        }

        if (isUserExist) {
            log.error("Попытка добавить несуществующего пользователя с id={} в друзья", friendId);
            throw new NotFoundException("Пользователя с id=" + friendId + " не существует");
        } else {
            log.error("Попытка добавить друга несуществующему пользователю с id={}", userId);
            throw new NotFoundException("Пользователя с id=" + userId + " не существует");
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Получен запрос на удаление друга с id={} из списка друзей пользователя с id={}", friendId, userId);

        boolean isUserExist = users.read(userId) != null;
        boolean isFriendExist = users.read(friendId) != null;

        log.trace("Пользователь есть в сторадже: {}, друг есть в сторадже: {}", isUserExist, isFriendExist);

        if (isUserExist && isFriendExist) {
            log.trace("Список друзей пользователя с id={} до удаления: {}", userId, users.read(userId).getFriends());
            log.trace("Друг с id={} есть в списке друзей пользователя с id={}: {}",
                    friendId, userId, users.read(userId).getFriends().contains(friendId));

            boolean isFriendRemoved = users.read(userId).getFriends().remove(friendId);
            if (isFriendRemoved) {
                log.info("Пользователь {} успешно удалил из друзей пользователя {}", userId, friendId);
            } else {
                log.warn("Друг с id={} не найден в списке друзей пользователя с id={}", friendId, userId);
            }

            log.trace("Список друзей пользователя с id={} после удаления: {}", userId, users.read(userId).getFriends());
            log.trace("Список друзей пользователя с id={} до удаления: {}", friendId, users.read(friendId).getFriends());
            log.trace("Друг с id={} есть в списке друзей пользователя с id={}: {}",
                    userId, friendId, users.read(friendId).getFriends().contains(userId));

            isFriendRemoved = users.read(friendId).getFriends().remove(userId);
            if (isFriendRemoved) {
                log.info("Пользователь {} успешно удалил из друзей пользователя {}", friendId, userId);
            } else {
                log.warn("Друг с id={} не найден в списке друзей пользователя с id={}", userId, friendId);
            }

            log.trace("Список друзей пользователя с id={} после удаления: {}",
                    friendId, users.read(friendId).getFriends());
            return;
        }
            if (isUserExist) {
                log.error("Попытка удаления из друзей не существующего пользователя с id={}", friendId);
                throw new NotFoundException("Пользователь с id=" + friendId + " не существует");
            } else {
                log.error("Попытка удаления из друзей не существующего пользователя с id={}", userId);
                throw new NotFoundException("Пользователь с id=" + userId + " не существует");
            }
    }

    public Collection<User> friends(Long id) {
        log.info("Получен запрос на получение списка друзей пользователя с id={}", id);

        if (users.read(id) != null) {
            User user = users.read(id);

            log.debug("Пользователь: {}, список друзей: {}", user, user.getFriends());
            return user.getFriends().stream()
                    .map(users::read)
                    .collect(Collectors.toSet());
        }

        log.error("Попытка получить список друзей несуществующего пользователя с id={}", id);
        throw new NotFoundException("Пользователь с id = " + id + " не найден");
    }

    public Collection<User> mutualFriends(Long userId, Long otherId) {
        log.info("Получен запрос на просмотр общих друзей пользователей с id={}, {}", userId, otherId);

        boolean isUserExist = users.read(userId) != null;
        boolean isOtherExist = users.read(otherId) != null;

        log.trace("Пользователь есть в сторадже: {}, другой пользователь есть в сторадже: {}",
                isUserExist, isOtherExist);

        if (isUserExist && isOtherExist) {
            Set<Long> intersection = new HashSet<>(users.read(userId).getFriends());
            intersection.retainAll(users.read(otherId).getFriends());
            return intersection.stream()
                    .map(users::read)
                    .collect(Collectors.toSet());
        }

        log.error("Попытка получить список общих друзей для несуществующего(их) пользователя(ей) с id={}, {}",
                userId, otherId);
        throw new NotFoundException("Передан несуществующий(ие) пользователь(ли)");
    }
}
