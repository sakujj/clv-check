package ru.clevertec.check;

public interface ProductRepository extends Repository<Product, Long> {
    void updateQuantityHavingId(Long id, int updateAmount);
}
