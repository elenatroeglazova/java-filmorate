package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.extractors.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;

import javax.sql.DataSource;
import java.util.*;

import static org.springframework.util.ObjectUtils.isEmpty;


@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_ALL_SQL = """
            SELECT f.*,
                   g.genres         AS genres,
                   l.liked_user_ids AS liked_user_ids,
                   r.id             AS rating_id,
                   r.name           AS rating_name
            FROM films f
            JOIN ratings r ON r.id = f.rating_id
            LEFT JOIN (
                SELECT fg.film_id,
                       array_agg(DISTINCT gen.id || ':' || COALESCE(gen.name, '')) AS genres
                FROM film_genre fg
                JOIN genres gen ON gen.id = fg.genre_id
                GROUP BY fg.film_id
            ) g ON g.film_id = f.id
            LEFT JOIN (
                SELECT ls.film_id,
                       array_agg(DISTINCT ls.user_id) AS liked_user_ids
                FROM likes ls
                GROUP BY ls.film_id
            ) l ON l.film_id = f.id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT f.*,
                   g.genres         AS genres,
                   l.liked_user_ids AS liked_user_ids,
                   r.id             AS rating_id,
                   r.name           AS rating_name
            FROM films f
            JOIN ratings r ON r.id = f.rating_id
            LEFT JOIN (
                SELECT fg.film_id,
                       array_agg(DISTINCT gen.id || ':' || COALESCE(gen.name, '')) AS genres
                FROM film_genre fg
                JOIN genres gen ON gen.id = fg.genre_id
                WHERE fg.film_id = :id
                GROUP BY fg.film_id
            ) g ON g.film_id = f.id
            LEFT JOIN (
                SELECT ls.film_id,
                       array_agg(DISTINCT ls.user_id) AS liked_user_ids
                FROM likes ls
                WHERE ls.film_id = :id
                GROUP BY ls.film_id
            ) l ON l.film_id = f.id
            WHERE f.id = :id
            """;

    private static final String UPDATE_FILM_SQL = """
            UPDATE films
            SET title        = :name,
                description  = :description,
                release_date = :releaseDate,
                duration     = :duration,
                rating_id    = :mpa.id
            WHERE id = :id
            """;

    private static final String DELETE_FILM_SQL = """
            DELETE FROM films WHERE id = :id
            """;

    private static final String INSERT_LIKE_SQL = """
            INSERT INTO likes (film_id, user_id)
            VALUES (:filmId, :userId)
            """;

    private static final String DELETE_LIKE_SQL = """
            DELETE FROM likes
            WHERE film_id = :filmId AND user_id = :userId
            """;

    private static final String FIND_ALL_GENRES_SQL = """
            SELECT * FROM genres
            """;

    private static final String FIND_BY_ID_GENRE_SQL = """
            SELECT * FROM genres
            WHERE id = :genreId
            """;

    private static final String FIND_ALL_RATINGS_SQL = """
            SELECT * FROM ratings
            ORDER BY id
            LIMIT 5
            """;

    private static final String FIND_BY_ID_RATING_SQL = """
            SELECT * FROM ratings
            WHERE id = :ratingId
            """;

    private static final String MERGE_FILM_GENRE_SQL = """
            MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id)
            VALUES (:filmId, :genreId)
            """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmResultSetExtractor extractor;
    private final DataSource dataSource;
    private final GenreRowMapper genreRowMapper;
    private final RatingRowMapper ratingRowMapper;

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
        parameters.put("DURATION", film.getDurationInMinutes());
        parameters.put("RATING_ID", film.getMpa().getId());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();

        if (id == 0) {
            throw new RuntimeException("Фильм не был создан по неизвестной причине");
        }

        addFilmGenres(id, genres);
        film.setId(id);

        return film;
    }

    @Override
    public void update(Film filmUpdate) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(filmUpdate);
        int updated = namedParameterJdbcTemplate.update(UPDATE_FILM_SQL, params);

        if (updated == 0) {
            throw new RuntimeException("Не удалось обновить фильм с id=" + filmUpdate.getId());
        }

        updateGenres(filmUpdate);
    }

    @Override
    public boolean delete(Long id) {
        int deleted = namedParameterJdbcTemplate.update(
                DELETE_FILM_SQL, new MapSqlParameterSource("id", id));

        return deleted > 0;
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
    public void addLike(Long filmId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);

        namedParameterJdbcTemplate.update(INSERT_LIKE_SQL, params);
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);

        int deleted = namedParameterJdbcTemplate.update(DELETE_LIKE_SQL, params);
        return deleted > 0;
    }

    @Override
    public Collection<Genre> genres() {
        return namedParameterJdbcTemplate.query(FIND_ALL_GENRES_SQL, genreRowMapper);
    }

    @Override
    public Collection<Rating> ratings() {
        return namedParameterJdbcTemplate.query(FIND_ALL_RATINGS_SQL, ratingRowMapper);
    }

    @Override
    public Genre findGenreById(Long genreId) {
        MapSqlParameterSource params = new MapSqlParameterSource("genreId", genreId);
        List<Genre> genres = namedParameterJdbcTemplate.query(FIND_BY_ID_GENRE_SQL, params, genreRowMapper);

        return genres.stream().findAny().orElseThrow(() ->
                new NotFoundException("Жанр по id=" + genreId + " не найден"));
    }

    @Override
    public Rating findRatingById(Long ratingId) {
        MapSqlParameterSource params = new MapSqlParameterSource("ratingId", ratingId);
        List<Rating> ratings = namedParameterJdbcTemplate.query(FIND_BY_ID_RATING_SQL, params, ratingRowMapper);

        return ratings.stream().findAny().orElseThrow(() ->
                new NotFoundException("Рейтинг по id=" + ratingId + " не найден"));
    }

    @Override
    public void clear() {

    }

    private void updateGenres(Film film) {
        Long filmId = film.getId();
        Set<Genre> genres = film.getGenres();

        namedParameterJdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id = :filmId",
                new MapSqlParameterSource("filmId", filmId));

        if (!isEmpty(genres)) {
            addFilmGenres(filmId, genres);
        }
    }

    private void addFilmGenres(Long filmId, Set<Genre> genres) {
        if (isEmpty(genres)) {
            return;
        }

        SqlParameterSource[] mappingParams = genres.stream()
                .map(g -> new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("genreId", g.getId()))
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(MERGE_FILM_GENRE_SQL, mappingParams);
    }
}
