package ru.clevertec.check;

import java.util.Map;
import java.util.Map.Entry;

public interface ProductResponseMapper {
    ProductResponse toResponse(Entry<ProductRequest, Product> productData, short discountPercentage);
}
