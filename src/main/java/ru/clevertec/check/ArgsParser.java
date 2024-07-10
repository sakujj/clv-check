package ru.clevertec.check;

import java.util.List;

public interface ArgsParser {

    record Args(
            List<ProductRequest> productRequests,
            DiscountCardRequest discountCardRequest,
            DebitCardRequest debitCardRequest
    ) {
    }

    Args parseArgs(String[] args);
}
