package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation_groups.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NotEmptyObject(groups = NotEmptyBody.class)
public class User {
    @Null(groups = OnCreate.class, message = "ID не должен быть указан")
    @NotNull(groups = OnUpdate.class, message = "ID должен быть указан")
    private Long id;

    @NotBlank(groups = CommonChecks.class, message = "Электронная почта пользователя не должна быть пустой")
    @Email(groups = CommonChecks.class, message = "Почта имеет некорректный формат")
    private String email;

    @NotBlank(groups = CommonChecks.class, message = "Логин не может быть пустым")
    @Pattern(groups = CommonChecks.class, regexp = "[\\w\\d]*",
            message = "Логин должен содержать только латинские буквы и цифры")
    private String login;

    @Pattern(groups = CommonChecks.class, regexp = "\\p{L}*",
            message = "Имя пользователя должно содержать только буквы")
    private String name;

    @Past(groups = CommonChecks.class, message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
