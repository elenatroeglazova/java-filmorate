package ru.yandex.practicum.filmorate.enums;

import lombok.Data;

public enum RatingMPA {
    G ("у фильма нет возрастных ограничений"),
    PG ("детям рекомендуется смотреть фильм с родителями"),
    PG_13 ("детям до 13 лет просмотр не желателен"),
    R ("лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC_17 ("лицам до 18 лет просмотр запрещён");

    private final String description;

    RatingMPA(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
