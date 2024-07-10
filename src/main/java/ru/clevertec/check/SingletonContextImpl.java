package ru.clevertec.check;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SingletonContextImpl implements SingletonContext {

    private final Map<Class<?>, Object> context = new HashMap<>();

    public SingletonContextImpl() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getByClass(Class<T> clazz) {
        return (T) context.get(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T put(Class<T> clazz, T instance) {
        return (T) context.putIfAbsent(clazz, instance);
    }

    @Override
    public void close() {

        for (Entry<Class<?>, Object> entry : context.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) value).close();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
