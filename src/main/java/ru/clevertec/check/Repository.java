package ru.clevertec.check;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    List<T> findAll();
    Page<T> find(PageRequest pageRequest);
    Optional<T> findById(ID id);
    void update(T product);
    ID add(T product);
    void removeById(ID id);
}
