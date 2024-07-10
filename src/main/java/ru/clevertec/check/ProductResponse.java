package ru.clevertec.check;

import java.math.BigDecimal;

public record ProductResponse(int quantity,
                              String description,
                              BigDecimal price,
                              BigDecimal discount,
                              BigDecimal total) {
}
