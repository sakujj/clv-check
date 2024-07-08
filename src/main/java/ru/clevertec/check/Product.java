package ru.clevertec.check;

import java.math.BigDecimal;

public record Product(
        long id,
        String description,
        BigDecimal price,
        int quantityInStock,
        boolean wholesaleProduct
) {
}
