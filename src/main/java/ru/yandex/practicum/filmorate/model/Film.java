package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.jackson.deserializers.DurationDeserializer;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Long id;

    private String name;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    @JsonGetter("duration")
    public long getDurationInMinutes() {
        return duration.toMinutes();
    }
}
