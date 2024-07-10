package ru.clevertec.check;

import java.time.LocalDateTime;
import java.util.List;

public record CheckResponse(LocalDateTime dateTimeIssuedOn,
                            List<ProductResponse> boughtProducts,
                            DiscountCardResponse usedDiscountCardIfAny) {
}
