package ru.yandex.practicum.filmorate.service;

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
public class FilmService {
    private final FilmStorage films = new InMemoryFilmStorage();

    public Collection<Film> films() {
        log.info("Получен запрос на получение всех фильмов. Всего фильмов: {}", films.size());
        log.debug("Список фильмов: {}", films.values());
        return films.values();
    }

    public Optional<Film> getById(Long id) {
        return films.values().stream()
                .filter(f -> f.getId().equals(id))
                .findAny();
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

        film.setId(IdGenerator.getNextId(films.keySet()));
        films.put(film.getId(), film);
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

        if (films.containsKey(filmUpdate.getId())) {
            log.debug("Найден фильм по id");
            Film currentFilm = films.get(filmUpdate.getId());
            log.debug("""
                            Данные найденного фильма:
                            Название фильма {}
                            Описание фильма {}
                            Дата выпуска фильма {}
                            Продолжительность фильма {}""",
                    currentFilm.getName(), currentFilm.getDescription(),
                    currentFilm.getReleaseDate(), currentFilm.getDuration());

            currentFilm.setDescription(filmUpdate.getDescription());
            currentFilm.setName(filmUpdate.getName());
            currentFilm.setReleaseDate(filmUpdate.getReleaseDate());
            currentFilm.setDuration(filmUpdate.getDuration());
            log.info("Фильм с id={} обновлен, новое название='{}'", filmUpdate.getId(), filmUpdate.getName());
            return currentFilm;
        }

        log.error("Попытка обновить несуществующий фильм с id={}", filmUpdate.getId());
        throw new NotFoundException("Фильм с id = " + filmUpdate.getId() + " не найден");
    }

    public void like(Long filmId, Long userId) {
        if (films.containsKey(filmId)) {
            films.get(filmId).getLikes().add(userId);
        }

        log.error("Попытка поставить лайк несуществующему фильму с id={}", filmId);
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    public void dislike(Long filmId, Long userId) {
        if (films.containsKey(filmId)) {
            films.get(filmId).getLikes().remove(userId);
        }

        log.error("Попытка удалить лайк у несуществующего фильма с id={}", filmId);
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    public Collection<Film> getMostPopular(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toSet());
    }
}
