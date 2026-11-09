package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Transactional(propagation = Propagation.MANDATORY)
    public void record(Long userId, EventType type, EventOperation operation, Long entityId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEventType(type);
        event.setOperation(operation);
        event.setEntityId(entityId);
        event.setTimestamp(System.currentTimeMillis());
        eventStorage.create(event);
    }

    @Transactional(readOnly = true)
    public Collection<Event> getFeed(Long userId) {
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        return eventStorage.findByUserId(userId);
    }
}
