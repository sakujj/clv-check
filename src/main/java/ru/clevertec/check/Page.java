package ru.clevertec.check;

import java.util.List;

public record Page<T>(int size, int number, List<T> data) {
}
