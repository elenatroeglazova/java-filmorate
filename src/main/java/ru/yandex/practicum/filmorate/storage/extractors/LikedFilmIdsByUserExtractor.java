package ru.yandex.practicum.filmorate.storage.extractors;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class LikedFilmIdsByUserExtractor implements ResultSetExtractor<Map<Long, List<Long>>> {

    @Override
    public Map<Long, List<Long>> extractData(ResultSet rs) throws SQLException {
        Map<Long, List<Long>> result = new LinkedHashMap<>();

        while (rs.next()) {
            Long userId = rs.getLong("user_id");
            List<Long> filmIds = new ArrayList<>();

            Object[] filmArray = getArrayFromColumn(rs, "film_ids");
            if (filmArray != null) {
                for (Object obj : filmArray) {
                    if (obj != null) {
                        filmIds.add(((Number) obj).longValue());
                    }
                }
            }

            result.put(userId, filmIds);
        }

        return result;
    }

    private Object[] getArrayFromColumn(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        return switch (value) {
            case null -> null;
            case java.sql.Array array -> (Object[]) array.getArray();
            case Object[] objects -> objects;
            default -> throw new SQLException("Неожиданный тип для колонки " + columnName + ": " + value.getClass());
        };
    }
}
