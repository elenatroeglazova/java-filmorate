package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final EventService eventService;
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Transactional
    public Review create(Review review) {
        userStorage.findById(review.getUserId()).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + review.getUserId() + " не найден"));
        filmStorage.findById(review.getFilmId()).orElseThrow(() ->
                new NotFoundException("Фильм с id = " + review.getFilmId() + " не найден"));
        Review created = reviewStorage.create(review);
        eventService.record(created.getUserId(), EventType.REVIEW, EventOperation.ADD, created.getReviewId());
        return created;
    }

    @Transactional
    public Review update(Review review) {
        reviewStorage.findById(review.getReviewId()).orElseThrow(() ->
                new NotFoundException("Отзыв с id = " + review.getReviewId() + " не найден"));
        reviewStorage.update(review);
        Review updated = reviewStorage.findById(review.getReviewId()).orElseThrow(() ->
                new NotFoundException("Отзыв с id = " + review.getReviewId() + " не найден"));
        eventService.record(updated.getUserId(), EventType.REVIEW, EventOperation.UPDATE, updated.getReviewId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        Review review = reviewStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Отзыв с id = " + id + " не найден"));
        if (!reviewStorage.delete(id)) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }
        eventService.record(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, id);
    }

    public Review getById(Long id) {
        return reviewStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Отзыв с id = " + id + " не найден"));
    }

    public Collection<Review> findAll(Long filmId, int count) {
        return reviewStorage.findAll(filmId, count);
    }

    @Transactional
    public void setVote(Long id, Long userId, int vote) {
        reviewStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Отзыв с id = " + id + " не найден"));
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        reviewStorage.setVote(id, userId, vote);
    }

    @Transactional
    public void removeVote(Long id, Long userId) {
        reviewStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Отзыв с id = " + id + " не найден"));
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        reviewStorage.removeVote(id, userId);
    }
}
