package search;

import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public record Found(int index) implements SearchResult {
    public Found {
        if (index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }
    }

    @Override
    public <T> T map(IntFunction<? extends T> foundMapper, Supplier<? extends T> notFoundSupplier) {
        Objects.requireNonNull(foundMapper, "foundMapper must not be null");
        return foundMapper.apply(index);
    }

    @Override
    public int orElse(int defaultIndex) {
        return index;
    }
}
