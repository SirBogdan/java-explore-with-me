package ru.practicum.utils;

import ru.practicum.exceptions.ValidationException;

import java.util.Objects;

public class NPEChecker {

    public static void checkObjNullValue(Object... o) {
        for (Object value : o) {
            if (Objects.isNull(value)) {
                throw new ValidationException("Ошибка: входные данные переданы с null - значением");
            }
        }
    }
}
