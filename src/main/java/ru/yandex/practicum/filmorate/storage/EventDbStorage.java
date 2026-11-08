package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import javax.sql.DataSource;
import java.util.Collection;

@Repository
public class EventDbStorage implements EventStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EventRowMapper rowMapper;
    private final SimpleJdbcInsert eventInsert;

    public EventDbStorage(
            NamedParameterJdbcTemplate jdbcTemplate,
            DataSource dataSource,
            EventRowMapper rowMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;

        this.eventInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("events")
                .usingColumns(
                        "user_id",
                        "event_type",
                        "operation",
                        "entity_id",
                        "event_timestamp"
                )
                .usingGeneratedKeyColumns("event_id");
    }

    @Override
    public Event create(Event event) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", event.getUserId())
                .addValue("event_type", event.getEventType().name())
                .addValue("operation", event.getOperation().name())
                .addValue("entity_id", event.getEntityId())
                .addValue("event_timestamp", event.getTimestamp());

        long eventId = eventInsert.executeAndReturnKey(parameters).longValue();

        event.setEventId(eventId);

        return event;
    }

    @Override
    public Collection<Event> findByUserId(long userId) {
        String sql = """
                SELECT *
                FROM events
                WHERE user_id = :userId
                ORDER BY event_timestamp DESC, event_id DESC
                """;

        return jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("userId", userId),
                rowMapper
        );
    }
}
