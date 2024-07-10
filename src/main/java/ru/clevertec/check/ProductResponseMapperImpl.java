package ru.clevertec.check;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map.Entry;

public class ProductResponseMapperImpl implements ProductResponseMapper{

    private final static short WHOLESALE_DISCOUNT_PERCENTAGE = 10;
    private final static int WHOLESALE_MIN_COUNT_TO_ACTIVATE_DISCOUNT = 5;

    public ProductResponse toResponse(Entry<ProductRequest, Product> productData, short discountPercentage) {

        ProductRequest productRequest = productData.getKey();
        Product product = productData.getValue();

        boolean isWholesaleProduct = product.wholesaleProduct();
        if (isWholesaleProduct && productRequest.quantity() >= WHOLESALE_MIN_COUNT_TO_ACTIVATE_DISCOUNT) {
            discountPercentage = WHOLESALE_DISCOUNT_PERCENTAGE;
        }

        int requestedQuantity = productRequest.quantity();
        BigDecimal priceForOne = product.price();

        BigDecimal totalWithoutDiscount = priceForOne
                .multiply(BigDecimal.valueOf(requestedQuantity))
                .setScale(2, RoundingMode.CEILING);

        BigDecimal totalDiscount = totalWithoutDiscount
                .multiply(BigDecimal.valueOf(discountPercentage))
                .divide(BigDecimal.valueOf(100), RoundingMode.CEILING)
                .setScale(2, RoundingMode.CEILING);

        return new ProductResponse(
                requestedQuantity,
                product.description(),
                priceForOne,
                totalDiscount,
                totalWithoutDiscount
        );
    }

}
