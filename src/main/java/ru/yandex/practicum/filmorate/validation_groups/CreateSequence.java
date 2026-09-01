package ru.yandex.practicum.filmorate.validation_groups;

import jakarta.validation.GroupSequence;

@GroupSequence({NotEmptyBody.class, OnCreate.class})
public interface CreateSequence {
}
