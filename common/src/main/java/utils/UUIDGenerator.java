package utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * UUID生成器
 */
public class UUIDGenerator {

    private static AtomicLong id = new AtomicLong();

    public static final long getNextId() {
        return id.getAndIncrement();
    }
}
