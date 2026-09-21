package ru.yandex.practicum.filmorate.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class FilmsBaseTest extends BaseTest {

    @BeforeAll
    public void setUp() throws IOException, InterruptedException {
        createFilms();
    }
}
