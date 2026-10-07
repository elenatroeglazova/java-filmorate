package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.extractors.FilmResultSetExtractor;

import javax.sql.DataSource;
import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_ALL_SQL = """
            SELECT f.*,
                   g.genres         AS genres,
                   l.liked_user_ids AS liked_user_ids,
                   r.id             AS rating_id,
                   r.name           AS rating_name
            FROM FILMS f
            JOIN ratings r ON r.id = m.rating_id
            JOIN (
                SELECT fg.film_id,
                       array_agg(DISTINCT gen.id || ':' || COALESCE(gen.name, '')) AS genres
                FROM FILM_GENRE fg
                JOIN GENRES gen ON gen.id = fg.genre_id
                GROUP BY fg.film_id
            ) g ON g.film_id = f.id
            LEFT JOIN (
                SELECT ls.film_id,
                       array_agg(DISTINCT ls.user_id) AS liked_user_ids
                FROM LIKES ls
                GROUP BY ls.film_id
            ) l ON l.film_id = f.id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT f.*,
                   g.genres         AS genres,
                   l.liked_user_ids AS liked_user_ids,
                   r.id             AS rating_id,
                   r.name           AS rating_name
            FROM FILMS f
            JOIN ratings r ON r.id = m.rating_id
            JOIN (
                SELECT fg.film_id,
                       array_agg(DISTINCT gen.id || ':' || COALESCE(gen.name, '')) AS genres
                FROM FILM_GENRE fg
                JOIN GENRES gen ON gen.id = fg.genre_id
                WHERE fg.film_id = :id
                GROUP BY fg.film_id
            ) g ON g.film_id = f.id
            LEFT JOIN (
                SELECT ls.film_id,
                       array_agg(DISTINCT ls.user_id) AS liked_user_ids
                FROM LIKES ls
                WHERE ls.film_id = :id
                GROUP BY ls.film_id
            ) l ON l.film_id = f.id
            WHERE f.id = :id
            """;
    private static final String MERGE_GENRE_SQL = """
        MERGE INTO genres (id, name) KEY(id)
        VALUES (:id, :name)
        """;

    private static final String MERGE_FILM_GENRE_SQL = """
        MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id)
        VALUES (:filmId, :genreId)
        """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmResultSetExtractor extractor;
    private final DataSource dataSource;

    @Override
    public Film create(Film film) {
        Set<Genre> genres = film.getGenres();

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("ID");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("TITLE", film.getName());
        parameters.put("DESCRIPTION", film.getDescription());
        parameters.put("RELEASE_DATE", film.getReleaseDate());
        parameters.put("DURATION", film.getDuration());
        parameters.put("RATING_ID", film.getMpa().getId());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();

        if (id == 0) {
            throw new IllegalStateException("id фильма не может быть 0");
        }

        film.setId(id);

        return film;
    }

    @Override
    public Film update(Long id, Film filmUpdate) {
        return null;
    }

    @Override
    public Film delete(Long id) {
        return null;
    }

    @Override
    public Collection<Film> findAll() {
        return namedParameterJdbcTemplate.query(FIND_ALL_SQL, extractor);
    }

    @Override
    public Optional<Film> findById(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<Film> films = namedParameterJdbcTemplate.query(FIND_BY_ID_SQL, params, extractor);

        return Optional.ofNullable(films)
                .filter(list -> !list.isEmpty())
                .map(List::getFirst);
    }

    @Override
    public void clear() {

    }
}
