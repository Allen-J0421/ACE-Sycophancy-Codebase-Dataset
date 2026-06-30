package search;

import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public record NotFound() implements SearchResult {
    @Override
    public <T> T map(IntFunction<? extends T> foundMapper, Supplier<? extends T> notFoundSupplier) {
        Objects.requireNonNull(notFoundSupplier, "notFoundSupplier must not be null");
        return notFoundSupplier.get();
    }

    @Override
    public int orElse(int defaultIndex) {
        return defaultIndex;
    }
}
