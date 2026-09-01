package ru.yandex.practicum.filmorate.validation_groups;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class NotEmptyObjectValidator implements ConstraintValidator<NotEmptyObject, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) return false;
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    return true;
                }
            } catch (IllegalAccessException ignored) {}
        }
        return false;
    }
}
