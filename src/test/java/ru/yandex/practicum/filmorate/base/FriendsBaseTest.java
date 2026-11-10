package ru.yandex.practicum.filmorate.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class FriendsBaseTest extends BaseTest {

    @BeforeAll
    protected void setUp() {
        createUsers();
        addFriends();
    }

    private void addFriends() {
        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(5L, 6L);
        userStorage.addFriend(5L, 1L);
        userStorage.addFriend(6L, 1L);
    }
}
