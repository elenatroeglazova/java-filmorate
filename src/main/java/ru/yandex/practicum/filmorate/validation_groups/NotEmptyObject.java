package ru.yandex.practicum.filmorate.validation_groups;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyObjectValidator.class)
public @interface NotEmptyObject {
    String message() default "Тело запроса не должно быть пустым";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
