package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.isNull;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmRowMapper mapper;

    @Override
    public Film create(Film value) {
        return null;
    }

    @Override
    public Film update(Long key, Film newValue) {
        return null;
    }

    @Override
    public Film delete(Long key) {
        return null;
    }

    @Override
    public Collection<Film> findAll() {
        return namedParameterJdbcTemplate.query(
                "SELECT * FROM FILMS", mapper
        );
    }

    @Override
    public Optional<Film> findById(Long key) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", key);
        Film film = namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM FILMS WHERE ID = :key", namedParameters, mapper);

        if (isNull(film)) {
            return Optional.empty();
        }

        return Optional.of(film);
    }

    @Override
    public void clear() {

    }
}
