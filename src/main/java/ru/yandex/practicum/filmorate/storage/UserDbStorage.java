package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.extractors.UserResultSetExtractor;

import javax.sql.DataSource;
import java.util.*;

@Primary
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String FIND_ALL_SQL = """
            SELECT u.*,
                   f.friends AS friends
            FROM users u
            LEFT JOIN (
                SELECT fr.user_id,
                       array_agg(DISTINCT fr.friend_id || ':' || COALESCE(fs.status, '')) AS friends
                FROM friends fr
                JOIN friend_status fs ON fs.id = fr.status_id
                GROUP BY fr.user_id
            ) f ON f.user_id = u.id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT u.*,
                   f.friends AS friends
            FROM users u
            LEFT JOIN (
                SELECT fr.user_id,
                       array_agg(DISTINCT fr.friend_id || ':' || COALESCE(fs.status, '')) AS friends
                FROM friends fr
                JOIN friend_status fs ON fs.id = fr.status_id
                WHERE fr.user_id = :id
                GROUP BY fr.user_id
            ) f ON f.user_id = u.id
            WHERE u.id = :id
            """;

    private static final String UPDATE_USER_SQL = """
            UPDATE users
            SET email    = :email,
                login    = :login,
                name     = :name,
                birthday = :birthday
            WHERE id = :id
            """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserResultSetExtractor extractor;
    private final DataSource dataSource;

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("ID");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("EMAIL", user.getEmail());
        parameters.put("LOGIN", user.getLogin());
        parameters.put("NAME", user.getName());
        parameters.put("BIRTHDAY", user.getBirthday());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();

        if (id == 0) {
            throw new RuntimeException("Пользователь не был создан по неизвестной причине");
        }

        user.setId(id);
        return user;
    }

    @Override
    public void update(User userUpdate) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(userUpdate);
        int updated = namedParameterJdbcTemplate.update(UPDATE_USER_SQL, params);

        if (updated == 0) {
            throw new RuntimeException("Не удалось обновить пользователя id=" + userUpdate.getId());
        }
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Collection<User> findAll() {
        return namedParameterJdbcTemplate.query(FIND_ALL_SQL, extractor);
    }

    @Override
    public Optional<User> findById(Long id) {
        List<User> users = namedParameterJdbcTemplate.query(
                FIND_BY_ID_SQL,
                new MapSqlParameterSource("id", id),
                extractor);

        return Optional.ofNullable(users)
                .filter(list -> !list.isEmpty())
                .map(List::getFirst);
    }

    @Override
    public Collection<User> friends(Long id) {
        return List.of();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public void clear() {

    }
}
