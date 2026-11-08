package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventFeedTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private UserService users;
    @Autowired
    private FilmService films;
    @Autowired
    private ReviewService reviews;
    @Autowired
    private EventStorage events;

    private long userId;
    private long friendId;
    private long filmId;

    @BeforeEach
    void setUp() {
        userId = createUser("author");
        friendId = createUser("friend");
        filmId = new SimpleJdbcInsert(jdbc).withTableName("films").usingGeneratedKeyColumns("id")
                .executeAndReturnKey(Map.of("title", "Фильм", "release_date", LocalDate.of(2020, 1, 1),
                        "duration", 100, "rating_id", 1L)).longValue();
    }

    @Test
    void emptyFeedAndInvalidUser() throws Exception {
        mvc.perform(get("/users/{id}/feed", userId))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/users/{id}/feed", Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(get("/users/0/feed")).andExpect(status().isBadRequest());
        mvc.perform(get("/users/-1/feed")).andExpect(status().isBadRequest());
    }

    @Test
    void friendshipAndLikesProduceEventsOnlyForChanges() throws Exception {
        long before = System.currentTimeMillis();
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/users/{id}/friends/{friend}", userId, friendId)).andExpect(status().isOk());
            mvc.perform(put("/films/{id}/like/{user}", filmId, userId)).andExpect(status().isOk());
        }
        for (int i = 0; i < 2; i++) {
            mvc.perform(delete("/users/{id}/friends/{friend}", userId, friendId)).andExpect(status().isOk());
            mvc.perform(delete("/films/{id}/like/{user}", filmId, userId)).andExpect(status().isOk());
        }
        List<Event> feed = List.copyOf(events.findByUserId(userId));
        assertThat(feed).hasSize(4);
        assertThat(feed).extracting(Event::getUserId).containsOnly(userId);
        assertThat(feed).extracting(Event::getEventType)
                .containsExactly(EventType.FRIEND, EventType.LIKE, EventType.FRIEND, EventType.LIKE);
        assertThat(feed).extracting(Event::getOperation)
                .containsExactly(EventOperation.ADD, EventOperation.ADD, EventOperation.REMOVE, EventOperation.REMOVE);
        assertThat(feed).extracting(Event::getEntityId).containsExactly(friendId, filmId, friendId, filmId);
        assertThat(feed).allSatisfy(event -> {
            assertThat(event.getTimestamp()).isBetween(before, System.currentTimeMillis());
            assertThat(event.getEventId()).isPositive();
        });
        assertThat(events.findByUserId(friendId)).isEmpty();
        mvc.perform(get("/users/{id}/feed", userId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(friendId))
                .andExpect(jsonPath("$[0].eventId").value(feed.getFirst().getEventId()))
                .andExpect(jsonPath("$[0].timestamp").value(feed.getFirst().getTimestamp()));
    }

    @Test
    void reviewLifecycleUsesPersistedAuthorAndSurvivesDeletion() throws Exception {
        Review created = newReview();
        String response = mvc.perform(post("/reviews").contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode body = objectMapper.readTree(response);
        long reviewId = body.get("reviewId").asLong();
        created.setReviewId(reviewId);
        created.setUserId(friendId);
        created.setContent("Обновлённый отзыв");
        mvc.perform(put("/reviews").contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created))).andExpect(status().isOk());
        mvc.perform(put("/reviews/{id}/like/{user}", reviewId, friendId)).andExpect(status().isOk());
        mvc.perform(delete("/reviews/{id}/like/{user}", reviewId, friendId)).andExpect(status().isOk());
        mvc.perform(delete("/reviews/{id}", reviewId)).andExpect(status().isOk());

        List<Event> feed = List.copyOf(events.findByUserId(userId));
        assertThat(feed).hasSize(3);
        assertThat(feed).extracting(Event::getEventType).containsOnly(EventType.REVIEW);
        assertThat(feed).extracting(Event::getOperation)
                .containsExactly(EventOperation.ADD, EventOperation.UPDATE, EventOperation.REMOVE);
        assertThat(feed).extracting(Event::getEntityId).containsOnly(reviewId);
        assertThat(events.findByUserId(friendId)).isEmpty();
    }

    @Test
    void feedReturnsMixedEventsInPostmanScenarioOrder() throws Exception {
        mvc.perform(put("/users/{id}/friends/{friend}", userId, friendId)).andExpect(status().isOk());
        mvc.perform(delete("/users/{id}/friends/{friend}", userId, friendId)).andExpect(status().isOk());
        String response = mvc.perform(post("/reviews").contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReview())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long reviewId = objectMapper.readTree(response).get("reviewId").asLong();
        Review updated = newReview();
        updated.setReviewId(reviewId);
        updated.setContent("Обновлённый отзыв");
        mvc.perform(put("/reviews").contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated))).andExpect(status().isOk());
        mvc.perform(delete("/reviews/{id}", reviewId)).andExpect(status().isOk());
        mvc.perform(put("/films/{id}/like/{user}", filmId, userId)).andExpect(status().isOk());
        mvc.perform(delete("/films/{id}/like/{user}", filmId, userId)).andExpect(status().isOk());

        String feedResponse = mvc.perform(get("/users/{id}/feed", userId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<Event> feed = objectMapper.readerForListOf(Event.class).readValue(feedResponse);
        assertThat(feed).hasSize(7);
        assertThat(feed).extracting(Event::getUserId).containsOnly(userId);
        assertThat(feed).extracting(Event::getEventType).containsExactly(
                EventType.FRIEND, EventType.FRIEND, EventType.REVIEW, EventType.REVIEW,
                EventType.REVIEW, EventType.LIKE, EventType.LIKE);
        assertThat(feed).extracting(Event::getOperation).containsExactly(
                EventOperation.ADD, EventOperation.REMOVE, EventOperation.ADD, EventOperation.UPDATE,
                EventOperation.REMOVE, EventOperation.ADD, EventOperation.REMOVE);
        assertThat(feed).extracting(Event::getEntityId)
                .containsExactly(friendId, friendId, reviewId, reviewId, reviewId, filmId, filmId);
        assertThat(feed).extracting(Event::getTimestamp).isSorted();
        assertThat(feed).extracting(Event::getEventId).isSorted();
    }

    @Test
    void failedOperationsDoNotProduceEvents() throws Exception {
        mvc.perform(put("/users/{id}/friends/{friend}", userId, Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(put("/films/{id}/like/{user}", Long.MAX_VALUE, userId)).andExpect(status().isNotFound());
        mvc.perform(put("/films/{id}/like/{user}", filmId, Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(delete("/reviews/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
        assertThat(events.findByUserId(userId)).isEmpty();
    }

    @Test
    void storageOrdersByTimestampThenIdAndFiltersByUser() {
        Event first = storeEvent(userId, 100L, EventType.LIKE, EventOperation.ADD);
        Event second = storeEvent(userId, 100L, EventType.REVIEW, EventOperation.UPDATE);
        Event older = storeEvent(userId, 50L, EventType.FRIEND, EventOperation.REMOVE);
        storeEvent(friendId, 200L, EventType.LIKE, EventOperation.ADD);
        assertThat(events.findByUserId(userId)).containsExactly(older, first, second);
    }

    @ParameterizedTest
    @ValueSource(strings = {"friendAdd", "friendRemove", "likeAdd", "likeRemove",
            "reviewAdd", "reviewUpdate", "reviewRemove"})
    void eventStorageFailureRollsBackBusinessOperation(String action) {
        if (action.equals("friendRemove")) {
            users.addFriend(userId, friendId);
        }
        if (action.equals("likeRemove")) {
            films.like(filmId, userId);
        }
        Review review = action.equals("reviewUpdate") || action.equals("reviewRemove")
                ? reviews.create(newReview()) : newReview();
        jdbc.update("DELETE FROM events WHERE user_id = ?", userId);
        // Reject only this user's events to exercise a real database insert failure.
        jdbc.execute("ALTER TABLE events ADD CONSTRAINT reject_test_events CHECK (user_id <> " + userId + ")");

        try {
            assertThatThrownBy(() -> {
                switch (action) {
                    case "friendAdd" -> users.addFriend(userId, friendId);
                    case "friendRemove" -> users.removeFriend(userId, friendId);
                    case "likeAdd" -> films.like(filmId, userId);
                    case "likeRemove" -> films.removeLike(filmId, userId);
                    case "reviewAdd" -> reviews.create(review);
                    case "reviewUpdate" -> {
                        review.setContent("Changed");
                        reviews.update(review);
                    }
                    case "reviewRemove" -> reviews.delete(review.getReviewId());
                    default -> throw new AssertionError(action);
                }
            }).isInstanceOf(DataIntegrityViolationException.class);
        } finally {
            jdbc.execute("ALTER TABLE events DROP CONSTRAINT reject_test_events");
        }

        assertThat(events.findByUserId(userId)).isEmpty();
        if (action.startsWith("friend")) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?",
                    Integer.class, userId, friendId)).isEqualTo(action.equals("friendAdd") ? 0 : 1);
        } else if (action.startsWith("like")) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                    Integer.class, filmId, userId)).isEqualTo(action.equals("likeAdd") ? 0 : 1);
        } else if (action.equals("reviewAdd")) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM reviews WHERE user_id = ?",
                    Integer.class, userId)).isZero();
        } else {
            assertThat(reviews.getById(review.getReviewId()).getContent()).isEqualTo("Отзыв");
        }
    }

    private long createUser(String login) {
        return new SimpleJdbcInsert(jdbc).withTableName("users").usingGeneratedKeyColumns("id")
                .executeAndReturnKey(Map.of("email", login + "@example.com", "login", login)).longValue();
    }

    private Review newReview() {
        return Review.builder().userId(userId).filmId(filmId).content("Отзыв").isPositive(true).build();
    }

    private Event storeEvent(long authorId, long timestamp, EventType type, EventOperation operation) {
        Event event = new Event();
        event.setUserId(authorId);
        event.setTimestamp(timestamp);
        event.setEventType(type);
        event.setOperation(operation);
        event.setEntityId(filmId);
        return events.create(event);
    }
}
