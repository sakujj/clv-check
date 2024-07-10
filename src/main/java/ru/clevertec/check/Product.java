package ru.clevertec.check;

import java.math.BigDecimal;

public record Product(
        long id,
        String description,
        BigDecimal price,
        int quantityInStock,
        boolean wholesaleProduct
) {

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {

        long id;
        String description;
        BigDecimal price;
        int quantityInStock;
        boolean wholesaleProduct;

        private ProductBuilder() {}

        public ProductBuilder id(long id){
            this.id = id;
            return this;
        }

        public ProductBuilder description(String description){
            this.description = description;
            return this;
        }

        public ProductBuilder price(BigDecimal price){
            this.price = price;
            return this;
        }

        public ProductBuilder quantityInStock(int quantityInStock){
            this.quantityInStock = quantityInStock;
            return this;
        }

        public ProductBuilder wholesaleProduct(boolean wholesaleProduct){
            this.wholesaleProduct = wholesaleProduct;
            return this;
        }

        public Product build() {
            return new Product(id, description, price, quantityInStock, wholesaleProduct);
        }
    }
}
