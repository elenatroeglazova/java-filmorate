package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class FilmService {
    private final EventService eventService;
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> films() {
        log.info("Получен запрос на получение всех фильмов.");

        Collection<Film> all = filmStorage.findAll();
        log.debug("Всего фильмов: {}. Список фильмов: {}", all.size(), all);
        return all;
    }

    public Optional<Film> getById(Long id) {
        log.debug("Поиск фильма по id={}", id);
        Optional<Film> film = filmStorage.findById(id);
        film.ifPresentOrElse(
                f -> log.debug("Фильм найден: {}", f),
                () -> log.debug("Фильм с id={} не найден", id)
        );

        return film;
    }

    public Film create(Film film) {
        log.info("Получен запрос на создание фильма с названием '{}'", film.getName());
        log.debug("""
                        Данные нового фильма:
                        Название фильма {}
                        Описание фильма {}
                        Дата выпуска фильма {}
                        Продолжительность фильма {}""",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        Film newFilm = filmStorage.create(film);
        log.info("Фильм создан: id={}, название='{}'", newFilm.getId(), newFilm.getName());
        return newFilm;
    }

    public Film update(Film filmUpdate) {
        log.info("Получен запрос на обновление фильма с id={}", filmUpdate.getId());
        log.debug("""
                        Данные для обновления фильма:
                        Название фильма {}
                        Описание фильма {}
                        Дата выпуска фильма {}
                        Продолжительность фильма {}""",
                filmUpdate.getName(), filmUpdate.getDescription(),
                filmUpdate.getReleaseDate(), filmUpdate.getDuration());

        Long updatedId = filmUpdate.getId();
        filmStorage.findById(updatedId).ifPresentOrElse(
                f -> log.debug("Найден фильм {} по id {}", f, updatedId),
                () -> {
                    log.error("Попытка обновить несуществующий фильм с id={}", updatedId);
                    throw new NotFoundException("Фильм с id = " + updatedId + " не найден");
                }
        );

        Film updatedFilm = filmStorage.update(filmUpdate);
        log.info("Фильм с id={} обновлен, новое название='{}'", updatedId, updatedFilm.getName());
        return updatedFilm;
    }

    public void delete(Long id) {
        log.info("Получен запрос на удаление фильма с id={}", id);
        if (!filmStorage.delete(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        log.info("Фильм с id={} удален", id);
    }

    @Transactional
    public void like(Long filmId, Long userId) {
        log.info("Получен запрос на добавление лайка фильму с id={} от пользователя с id={}", filmId, userId);

        userService.getById(userId).ifPresentOrElse(
                u -> log.debug("Пользователь {} найден по id={}", u, userId),
                () -> {
                    log.error("Попытка поставить лайк от несуществующему пользователя с id={}", userId);
                    throw new NotFoundException("Пользователь с id = " + userId + " не найден");
                }
        );

        filmStorage.findById(filmId).ifPresentOrElse(
                f -> {
                    log.debug("Фильм {} по id={}", f, filmId);
                    if (!f.getLikes().contains(userId)) {
                        filmStorage.addLike(filmId, userId);
                    }
                    eventService.record(userId, EventType.LIKE, EventOperation.ADD, filmId);
                    log.info("Поставлен лайк фильму id={} от пользователя id={}", filmId, userId);
                },
                () -> {
                    log.error("Попытка поставить лайк несуществующему фильму с id={}", filmId);
                    throw new NotFoundException("Фильм с id = " + filmId + " не найден");
                }
        );
    }

    @Transactional
    public void removeLike(Long filmId, Long userId) {
        log.info("Получен запрос на удаление лайка у фильма с id={} от пользователя с id={}", filmId, userId);
        userService.checkUser(userId, "Попытка удалить лайк от несуществующего пользователя с id={}");

        filmStorage.findById(filmId).ifPresentOrElse(
                f -> {
                    log.debug("Фильм {} найден по id={}", f, filmId);
                    boolean isRemoved = filmStorage.removeLike(filmId, userId);

                    if (isRemoved) {
                        eventService.record(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
                        log.info("Удален лайк от пользователя id={} у фильма id={}", userId, filmId);
                    } else {
                        log.warn("Лайк от пользователя id={} у фильма id={} не найден", userId, filmId);
                    }
                },
                () -> {
                    log.error("Попытка удалить лайк у несуществующего фильма с id={}", filmId);
                    throw new NotFoundException("Фильм с id = " + filmId + " не найден");
                }
        );
    }

    public Collection<Film> getMostPopular(int count, Long genreId, Integer year) {
        log.info("Получен запрос на получение {} самых популярных фильмов", count);

        Collection<Film> popular = filmStorage.mostPopular(count, genreId, year);

        log.debug("Найдено популярных фильмов: {}, из общего списка - {}", popular.size(), filmStorage.findAll().size());
        log.trace("Список фильмов: {}", filmStorage);
        log.trace("Список популярных фильмов: {}", popular);

        return popular;
    }

    public Collection<Film> getByDirectorId(Long directorId, String sortBy) {
        if (!"year".equalsIgnoreCase(sortBy) && !"likes".equalsIgnoreCase(sortBy)) {
            throw new ValidationException("Параметр sortBy должен быть 'year' или 'likes'");
        }
        return filmStorage.findByDirectorId(directorId, sortBy);
    }

    public List<Film> search(String query, String by) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Параметр query не может быть пустым");
        }
        if (by == null || by.isBlank()) {
            throw new IllegalArgumentException("Параметр by не может быть пустым");
        }
        String[] searchParams = by.split(",");
        for (String param : searchParams) {
            if (!param.trim().equals("title") && !param.trim().equals("director")) {
                throw new IllegalArgumentException("Параметр by может принимать значения: title, director");
            }
        }
        return filmStorage.search(query, by);
    }

    public Collection<Film> common(Long userId, Long friendId) {
        log.info("Получен запрос на поиск общего списка фильмов для пользователя с id={} и друга с id={}",
                userId, friendId);
        List<Long> ids = filmStorage.common(userId, friendId);

        return filmStorage.findByIds(ids);
    }
}
