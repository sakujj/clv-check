package ru.clevertec.check;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class SingletonContextFactoryImpl implements SingletonContextFactory {

    private volatile boolean isContextCreated = false;
    private final Lock lock = new ReentrantLock();
    private SingletonContext context;

    private final Consumer<SingletonContext> contextInitializer;

    public SingletonContextFactoryImpl(Consumer<SingletonContext> contextInitializer) {
        this.contextInitializer = contextInitializer;
    }

    /**
     * Creates new SingletonContext instance on the first invocation.
     * On subsequent calls returns this instance.
     * Thread-safe.
     *
     * @return SingletonContext instance related to this factory
     */
    @Override
    public SingletonContext createOrReturnExisting() {
        if (isContextCreated) {
            return context;
        }

        try {
            lock.lock();

            if (isContextCreated) {
                return context;
            }
            context = createContext();
            isContextCreated = true;
            return context;

        } finally {
            lock.unlock();
        }
    }

    private SingletonContext createContext() {
        SingletonContext context = new SingletonContextImpl();
        contextInitializer.accept(context);
        return context;
    }
}
