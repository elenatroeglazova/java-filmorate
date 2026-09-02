package ru.yandex.practicum.filmorate.validation_groups;

import jakarta.validation.GroupSequence;

@GroupSequence({NotEmptyBody.class, OnUpdate.class})
public interface UpdateSequence {
}
