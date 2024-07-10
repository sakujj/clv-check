package ru.clevertec.check;

public interface ProductFileRepository extends ProductRepository {
    void update(Product product, boolean doFlush);
    Long add(Product product, boolean doFlush);
    void removeById(Long id, boolean doFlush);
    void updateQuantityHavingId(Long id, int updateAmount, boolean doFlush);
    void flush();
}
