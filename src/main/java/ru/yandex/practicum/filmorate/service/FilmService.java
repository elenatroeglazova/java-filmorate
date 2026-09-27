package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage films = new InMemoryFilmStorage();
    private final UserService userService;

    public Collection<Film> films() {
        log.info("Получен запрос на получение всех фильмов. Всего фильмов: {}", films.findAll().size());
        log.debug("Список фильмов: {}", films.findAll());
        return films.findAll();
    }

    public Optional<Film> getById(Long id) {
        log.debug("Поиск фильма по id={}", id);
        Optional<Film> film = films.findAll().stream()
                .filter(f -> f.getId().equals(id))
                .findAny();
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

        film.setId(IdGenerator.getNextId(films.ids()));
        films.create(film.getId(), film);
        log.info("Фильм создан: id={}, название='{}'", film.getId(), film.getName());
        return film;
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
        if (films.read(updatedId) != null) {
            log.debug("Найден фильм по id");

            Film newFilm = films.update(updatedId, filmUpdate);
            log.info("Фильм с id={} обновлен, новое название='{}'", filmUpdate.getId(), filmUpdate.getName());
            return newFilm;
        }

        log.error("Попытка обновить несуществующий фильм с id={}", filmUpdate.getId());
        throw new NotFoundException("Фильм с id = " + filmUpdate.getId() + " не найден");
    }

    public void like(Long filmId, Long userId) {
        log.info("Получен запрос на добавление лайка фильму с id={} от пользователя с id={}", filmId, userId);

        boolean isFilmExist = films.read(filmId) != null;
        boolean isUserExist = userService.getById(userId).isPresent();

        log.trace("Фильм найден в сторадже: {}, пользователь найден в сторадже: {}", isFilmExist, isUserExist);

        if (isFilmExist && isUserExist) {
            films.read(filmId).getLikes().add(userId);
            return;
        }

        if (isFilmExist) {
            log.error("Попытка поставить лайк от несуществующему пользователя с id={}", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        } else {
            log.error("Попытка поставить лайк несуществующему фильму с id={}", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }


    }

    public void dislike(Long filmId, Long userId) {
        log.info("Получен запрос на удаление лайка у фильма с id={} от пользователя с id={}", filmId, userId);

        boolean isFilmExist = films.read(filmId) != null;
        boolean isUserExist = userService.getById(userId).isPresent();

        log.trace("Фильм найден в сторадже: {}, пользователь найден в сторадже: {}", isFilmExist, isUserExist);

        if (isFilmExist && isUserExist) {
            log.trace("Лайки от пользователей до удаления: " + films.read(filmId).getLikes());
            log.trace("Пользователь есть в списке лайков: " + films.read(filmId).getLikes().contains(userId));

            boolean isRemoved = films.read(filmId).getLikes().remove(userId);

            log.trace("Лайки от рользователей после удаления: " + films.read(filmId).getLikes());

            if (isRemoved) {
                log.info("Пользователь с id={} удалил лайк у фильма с id={}", userId, filmId);
            } else {
                log.warn("Лайк от пользователя с id={} у фильма с id={} не найден", userId, filmId);
            }

            return;
        }

        if (isFilmExist) {
            log.error("Попытка удалить лайк от несуществующего пользователя с id={}", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        } else {
            log.error("Попытка удалить лайк у несуществующего фильма с id={}", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    public Collection<Film> getMostPopular(int count) {
        log.info("Получен запрос на получение {} самых популярных фильмов", count);

        Collection<Film> popular = films.findAll().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());

        log.debug("Найдено популярных фильмов: {}, из общего списка - {}", popular.size(), films.findAll().size());
        log.trace("Список фильмов: {}", films);
        log.trace("Список популярных фильмов: {}", popular);

        return popular;
    }
}
