package search;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public sealed interface SearchResult permits Found, NotFound {
    <T> T map(IntFunction<? extends T> foundMapper, Supplier<? extends T> notFoundSupplier);

    int orElse(int defaultIndex);

    static SearchResult found(int index) {
        return new Found(index);
    }

    static SearchResult notFound() {
        return new NotFound();
    }
}
