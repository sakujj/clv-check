package ru.clevertec.check;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private final ProductFileRepository productFileRepository;
    private final ProductResponseMapper productResponseMapper;

    public ProductServiceImpl(ProductFileRepository productFileRepository,
                              ProductResponseMapper productResponseMapper) {
        this.productFileRepository = productFileRepository;
        this.productResponseMapper = productResponseMapper;
    }

    @Override
    public Map<ProductRequest, Product> checkIfAvailableAndReturnGrouped(List<ProductRequest> productRequests) {
        return productRequests.stream()
                .peek(pr -> {
                    Product product = productFileRepository.findById(pr.id())
                            .orElseThrow(() -> new BadRequestException(String.format("No product present with id %d", pr.id())));

                    if (product.quantityInStock() < pr.quantity()) {
                        throw new BadRequestException(
                                String.format("There are only %d units of a product with id %d, but %d were requested",
                                        product.quantityInStock(), pr.id(), pr.quantity()));
                    }
                })
                .map(pr -> Map.entry(pr, productFileRepository.findById(pr.id()).get()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));    }

    @Override
    public void updateProductQuantity(List<ProductRequest> productRequests) {
        productRequests.forEach(pr ->
                productFileRepository.updateQuantityHavingId(pr.id(), -pr.quantity(), false)
        );
        productFileRepository.flush();
    }

    @Override
    public void checkIfEnoughMoneySupplied(List<ProductResponse> productResponses, BigDecimal balance) {
        BigDecimal endPrice = productResponses.stream()
                .map(resp -> resp.total().subtract(resp.discount()))
                .reduce(BigDecimal::add)
                .orElseThrow(RuntimeException::new);


        if (endPrice.compareTo(balance) > 0) {
            throw new NotEnoughMoneyException(
                    String.format("Required %s$, but you have only %s$",
                            endPrice,
                            balance.toString()));
        }
    }

    @Override
    public List<ProductResponse> getProductResponsesForRequestedData(Map<ProductRequest, Product> requestedProductsData,
                                                                     DiscountCardResponse discountCardResponse) {
        return requestedProductsData.entrySet().stream()
                .map(e -> productResponseMapper.toResponse(e, discountCardResponse.percentage()))
                .collect(Collectors.toCollection(ArrayList<ProductResponse>::new));
    }
}
