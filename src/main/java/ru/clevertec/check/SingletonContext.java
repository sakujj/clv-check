package ru.clevertec.check;

public interface SingletonContext extends AutoCloseable {

    <T> T getByClass(Class<T> clazz);

    <T> T put(Class<T> clazz, T instance);
}
