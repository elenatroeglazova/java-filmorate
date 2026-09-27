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
        userStorage.read(1L).getFriends().add(2L);
        userStorage.read(5L).getFriends().add(6L);
        userStorage.read(5L).getFriends().add(1L);
        userStorage.read(6L).getFriends().add(1L);
    }
}
