package ru.clevertec.check;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<ProductRequest, Product> checkIfAvailableAndReturnGrouped(List<ProductRequest> productRequests);
    void updateProductQuantity(List<ProductRequest> productRequests);
    void checkIfEnoughMoneySupplied(List<ProductResponse> productResponses, BigDecimal balance);
    List<ProductResponse> getProductResponsesForRequestedData(Map<ProductRequest, Product> requestedProductsData,
                                                              DiscountCardResponse discountCardResponse);
}

