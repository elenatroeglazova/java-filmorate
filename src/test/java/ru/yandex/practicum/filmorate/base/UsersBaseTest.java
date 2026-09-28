package ru.yandex.practicum.filmorate.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class UsersBaseTest extends BaseTest {
    protected User baseUser;

    @BeforeAll
    protected void setUp() {
        baseUser = User.builder()
                .email("baseUser@email.ru")
                .login("baseUser")
                .name("Базовый пользователь")
                .birthday(LocalDate.of(2015, 8, 13))
                .build();

        userStorage.create(baseUser);
    }
}
