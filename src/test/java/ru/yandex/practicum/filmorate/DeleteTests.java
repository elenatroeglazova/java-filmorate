package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DeleteTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JdbcTemplate jdbc;

    private long userId;
    private long otherUserId;
    private long filmId;
    private long otherFilmId;
    private long reviewId;
    private long otherReviewId;
    private long directorId;

    @BeforeEach
    void setUp() {
        userId = insert("users", Map.of("email", "author@example.com", "login", "author"));
        otherUserId = insert("users", Map.of("email", "reader@example.com", "login", "reader"));
        filmId = insert("films", Map.of("title", "Фильм", "rating_id", 1));
        otherFilmId = insert("films", Map.of("title", "Другой фильм", "rating_id", 1));
        directorId = insert("directors", Map.of("name", "Режиссер"));
        for (long id : new long[]{filmId, otherFilmId}) {
            jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, 1)", id);
            jdbc.update("INSERT INTO film_director (film_id, director_id) VALUES (?, ?)", id, directorId);
            for (long uid : new long[]{userId, otherUserId}) {
                jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", id, uid);
            }
        }
        jdbc.update("INSERT INTO friends (user_id, friend_id, status_id) VALUES (?, ?, 1)", userId, otherUserId);
        jdbc.update("INSERT INTO friends (user_id, friend_id, status_id) VALUES (?, ?, 1)", otherUserId, userId);
        reviewId = insert("reviews", Map.of("content", "Отзыв", "is_positive", true,
                "user_id", userId, "film_id", filmId));
        otherReviewId = insert("reviews", Map.of("content", "Другой отзыв", "is_positive", false,
                "user_id", otherUserId, "film_id", otherFilmId));
        jdbc.update("INSERT INTO review_votes (review_id, user_id, vote) VALUES (?, ?, 1)", reviewId, otherUserId);
        jdbc.update("INSERT INTO review_votes (review_id, user_id, vote) VALUES (?, ?, -1)", otherReviewId, userId);
        for (long uid : new long[]{userId, otherUserId}) {
            jdbc.update("""
                    INSERT INTO events (user_id, event_type, operation, entity_id, event_timestamp)
                    VALUES (?, 'LIKE', 'ADD', ?, 1)
                    """, uid, filmId);
        }
    }

    @Test
    void deleteUserRemovesDependenciesAndPreservesOtherData() throws Exception {
        mvc.perform(delete("/users/{userId}", userId)).andExpect(status().isOk());

        assertCount("SELECT COUNT(*) FROM users WHERE id = ?", userId, 0);
        assertCount("SELECT COUNT(*) FROM users WHERE id = ?", otherUserId, 1);
        assertCount("SELECT COUNT(*) FROM friends WHERE user_id = ? OR friend_id = ?", userId, userId, 0);
        assertCount("SELECT COUNT(*) FROM likes WHERE user_id = ?", userId, 0);
        assertCount("SELECT COUNT(*) FROM likes WHERE user_id = ?", otherUserId, 2);
        assertCount("SELECT COUNT(*) FROM reviews WHERE id = ?", reviewId, 0);
        assertCount("SELECT COUNT(*) FROM reviews WHERE id = ?", otherReviewId, 1);
        assertCount("SELECT COUNT(*) FROM review_votes WHERE review_id = ?", reviewId, 0);
        assertCount("SELECT COUNT(*) FROM review_votes WHERE user_id = ?", userId, 0);
        assertCount("SELECT COUNT(*) FROM events WHERE user_id = ?", userId, 0);
        assertCount("SELECT COUNT(*) FROM events WHERE user_id = ?", otherUserId, 1);
        assertCount("SELECT COUNT(*) FROM films WHERE id = ?", filmId, 1);
        assertCount("SELECT COUNT(*) FROM film_genre WHERE film_id = ?", filmId, 1);
        assertCount("SELECT COUNT(*) FROM film_director WHERE film_id = ?", filmId, 1);
        mvc.perform(delete("/users/{userId}", userId)).andExpect(status().isNotFound());
    }

    @Test
    void deleteFilmRemovesDependenciesAndPreservesOtherData() throws Exception {
        mvc.perform(delete("/films/{filmId}", filmId)).andExpect(status().isOk());

        assertCount("SELECT COUNT(*) FROM films WHERE id = ?", filmId, 0);
        assertCount("SELECT COUNT(*) FROM films WHERE id = ?", otherFilmId, 1);
        assertCount("SELECT COUNT(*) FROM film_genre WHERE film_id = ?", filmId, 0);
        assertCount("SELECT COUNT(*) FROM film_director WHERE film_id = ?", filmId, 0);
        assertCount("SELECT COUNT(*) FROM likes WHERE film_id = ?", filmId, 0);
        assertCount("SELECT COUNT(*) FROM reviews WHERE id = ?", reviewId, 0);
        assertCount("SELECT COUNT(*) FROM review_votes WHERE review_id = ?", reviewId, 0);
        assertCount("SELECT COUNT(*) FROM likes WHERE film_id = ?", otherFilmId, 2);
        assertCount("SELECT COUNT(*) FROM film_genre WHERE film_id = ?", otherFilmId, 1);
        assertCount("SELECT COUNT(*) FROM film_director WHERE film_id = ?", otherFilmId, 1);
        assertCount("SELECT COUNT(*) FROM reviews WHERE id = ?", otherReviewId, 1);
        assertCount("SELECT COUNT(*) FROM review_votes WHERE review_id = ?", otherReviewId, 1);
        assertCount("SELECT COUNT(*) FROM users WHERE id = ?", userId, 1);
        assertCount("SELECT COUNT(*) FROM users WHERE id = ?", otherUserId, 1);
        assertCount("SELECT COUNT(*) FROM directors WHERE id = ?", directorId, 1);
        assertCount("SELECT COUNT(*) FROM genres WHERE id = ?", 1, 1);
        assertCount("SELECT COUNT(*) FROM ratings WHERE id = ?", 1, 1);
        mvc.perform(delete("/films/{filmId}", filmId)).andExpect(status().isNotFound());
    }

    @Test
    void rejectUnknownIds() throws Exception {
        mvc.perform(delete("/users/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(delete("/films/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
        assertCount("SELECT COUNT(*) FROM users WHERE id = ?", userId, 1);
        assertCount("SELECT COUNT(*) FROM films WHERE id = ?", filmId, 1);
    }

    @Test
    void rejectInvalidIds() throws Exception {
        for (String resource : new String[]{"users", "films"}) {
            for (String id : new String[]{"0", "-1", "invalid"}) {
                mvc.perform(delete("/{resource}/{id}", resource, id)).andExpect(status().isBadRequest());
            }
        }
    }

    private long insert(String table, Map<String, ?> values) {
        return new SimpleJdbcInsert(jdbc).withTableName(table).usingGeneratedKeyColumns("id")
                .executeAndReturnKey(values).longValue();
    }

    private void assertCount(String sql, long id, int expected) {
        assertThat(jdbc.queryForObject(sql, Integer.class, id)).isEqualTo(expected);
    }

    private void assertCount(String sql, long id, long otherId, int expected) {
        assertThat(jdbc.queryForObject(sql, Integer.class, id, otherId)).isEqualTo(expected);
    }
}
